package main.model.db.imports;

import lombok.SneakyThrows;
import main.exceptions.AqualityException;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.ImportDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.settings.UserDto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Importer extends BaseImporter {
    private List<String> files;
    private String type;
    private TestNameNodeType testNameNodeType;
    private String suiteName;
    private TestRunDto testRunTemplate;
    private boolean singleTestRun;
    private Date nextFinishTime = new Date();
    private static final Logger LOGGER = Logger.getLogger(Importer.class.getName());
    private final List<ImportDto> pendingImports = new ArrayList<>();
    private final Set<Integer> closedImports = new HashSet<>();
    private int currentImportIndex = -1;

    private HandlerFactory handlerFactory = new HandlerFactory();

    public Importer(List<String> files, TestRunDto testRunTemplate, String pattern, String type, TestNameNodeType testNameNodeType, boolean singleTestRun, UserDto user) throws AqualityException {
        super(testRunTemplate.getProject_id(), pattern, user);
        this.testRunTemplate = testRunTemplate;
        this.suiteName = testRunTemplate.getTest_suite().getName();
        this.files = files;
        this.type = type;
        this.testNameNodeType = testNameNodeType;
        this.singleTestRun = singleTestRun;
    }

    /**
     * Validates import settings and creates Import records with status "in progress"
     * so UI can show them immediately while executeImport() runs in background.
     * Validation errors are thrown here (before any record is created) so the caller
     * can report them synchronously.
     */
    public List<ImportDto> startImport() throws AqualityException {
        validateSettings();
        pendingImports.clear();
        closedImports.clear();
        currentImportIndex = -1;
        if (testRunTemplate.getId() == null && !singleTestRun) {
            for (String pathToFile : this.files) {
                File file = new File(pathToFile);
                pendingImports.add(createImport("Import was started for file: " + file.getName()));
            }
        } else {
            pendingImports.add(createImport("Import into One Test Run was started!"));
        }
        return new ArrayList<>(pendingImports);
    }

    /**
     * Marks every Import record that was not finished yet as failed.
     * Safety net for failures that happen outside of the regular import flow
     * (unexpected errors, rejected or dropped background tasks).
     */
    public void failUnfinishedImports(String reason) {
        for (int i = 0; i < pendingImports.size(); i++) {
            if (closedImports.contains(i)) {
                continue;
            }
            // the import being processed holds the freshest state (log, test run id) in importDto
            if (i != currentImportIndex) {
                importDto = pendingImports.get(i);
            }
            try {
                finishImportWithError(reason);
                closedImports.add(i);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Cannot mark import as failed: id=" + importDto.getId(), e);
            }
        }
    }

    public List<ImportDto> executeImport() throws AqualityException {
        if (pendingImports.isEmpty()) {
            startImport();
        }

        if (testRunTemplate.getId() == null && !singleTestRun) {
            return executeMultiTestRunImport();
        }
        return Collections.singletonList(executeSingleTestRunImport());
    }

    @SneakyThrows
    private ImportDto executeSingleTestRunImport() throws AqualityException {
        openImport(0);
        try {
            readData(this.files);
            executeResultsCreation();
            ImportDto finished = finishImport();
            closedImports.add(0);
            return finished;
        } catch (Exception e) {
            finishImportWithError(e.getMessage());
            closedImports.add(0);
            throw e;
        }
    }

    @SneakyThrows
    private List<ImportDto> executeMultiTestRunImport() throws AqualityException {
        List<ImportDto> imports = new ArrayList<>();
        for (int i = 0; i < this.files.size(); i++) {
            openImport(i);
            String pathToFile = this.files.get(i);
            try {
                File file = new File(pathToFile);
                readData(file);
                executeResultsCreation();
                imports.add(finishImport());
                closedImports.add(i);
            } catch (Exception e) {
                finishImportWithError(e.getMessage());
                closedImports.add(i);
                failRemainingImports(i + 1, e.getMessage());
                throw e;
            }
        }

        return imports;
    }

    private void openImport(int index) {
        currentImportIndex = index;
        importDto = pendingImports.get(index);
    }

    private void failRemainingImports(int fromIndex, String error) throws AqualityException {
        for (int i = fromIndex; i < pendingImports.size(); i++) {
            openImport(i);
            finishImportWithError("Skipped due to previous import error: " + error);
            closedImports.add(i);
        }
    }

    private void validateSettings() throws AqualityException {
        if (files == null || files.isEmpty()) {
            throw new AqualityParametersException("There are no files to import. Attach at least one file to the request.");
        }

        ImportTypes importType;
        try {
            importType = ImportTypes.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AqualityParametersException("Import type '%s' is not supported. Allowed values: %s",
                    type, Arrays.toString(ImportTypes.values()));
        }
        handlerFactory.validateTypeOnNameNodeRequirements(importType, testNameNodeType);
    }

    private void executeResultsCreation() throws AqualityException, IOException, URISyntaxException {
        this.processImport(testRunTemplate.getId() != null);
        this.testRun = new TestRunDto();
        this.testResults = new ArrayList<>();
        this.tests = new ArrayList<>();
    }


    private void readData(List<String> filePaths) throws AqualityException {
        for (String pathToFile : filePaths) {
            Handler handler = handlerFactory.getHandler(new File(pathToFile), type, testNameNodeType, nextFinishTime);
            updateTestRun(handler);
            storeResults(handler);
        }
        fillTestRunWithInputData();
        fillTestSuiteWithInputData();
    }

    private void readData(File file) throws AqualityException {
        Handler handler = handlerFactory.getHandler(file, type, testNameNodeType, new Date());
        storeResults(handler);
        fillTestRunWithInputData(file.getName());
        fillTestSuiteWithInputData();
    }

    private void storeResults(Handler handler) throws AqualityException {
        this.testRun = handler.getTestRun();
        this.testResults.addAll(handler.getTestResults());
        this.tests.addAll(handler.getTests());
        this.testSuite = handler.getTestSuite();
        logToImport("File was parsed correctly!");
    }

    private void updateTestRun(Handler handler) {
        TestRunDto handlerTestRun = handler.getTestRun();
        if (this.testRun != null) {

            if (this.testRun.getStart_time().before(handlerTestRun.getStart_time())) {
                handlerTestRun.setStart_time(this.testRun.getStart_time());
            }

            if (this.testRun.getFinish_time().after(handlerTestRun.getFinish_time())) {
                handlerTestRun.setFinish_time(this.testRun.getFinish_time());
            }
        }

        handler.setTestRun(handlerTestRun);
        nextFinishTime = handlerTestRun.getStart_time();
    }

    private void fillTestSuiteWithInputData() {
        testSuite.setName(suiteName);
    }

    private void fillTestRunWithInputData() {
        fillTestRunWithInputData(null);
    }

    private void fillTestRunWithInputData(String fileName) {
        testRun.setProject_id(this.projectId);
        testRun.setCi_build(testRunTemplate.getCi_build());
        this.testRun.setAuthor(testRunTemplate.getAuthor());
        this.testRun.setExecution_environment(testRunTemplate.getExecution_environment());
        this.testRun.setBuild_name(fileName == null ? testRunTemplate.getBuild_name() : getBuildName(testRunTemplate, fileName));
        this.testRun.setId(testRunTemplate.getId());
        this.testRun.setDebug(testRunTemplate.getDebug());
    }

    private String getBuildName(TestRunDto testRun, String fileName) {
        return (testRun.getBuild_name() != null && !testRun.getBuild_name().equals(""))
                ? testRun.getBuild_name()
                : fileName.substring(0, fileName.lastIndexOf("."));
    }

    private ImportDto finishImport() throws AqualityException {
        importDto.setFinished(new Date());
        importDto.setFinish_status(1);
        importDto.addToLog("Import was finished!");
        return importDao.create(importDto);
    }

    private void finishImportWithError(String log) throws AqualityException {
        if (log == null) {
            log = "Without any error message :(";
        }

        importDto.setProject_id(this.projectId);
        importDto.setFinished(new Date());
        importDto.setFinish_status(2);
        importDto.addToLog("Import was finished with Error! " + log);
        importDao.create(importDto);
    }

    private ImportDto createImport(String log) throws AqualityException {
        importDto = new ImportDto();
        importDto.setStarted(new Date());
        importDto.setProject_id(projectId);
        importDto.setFinish_status(0);
        importDto.setLog(log);
        importDto = importDao.create(importDto);
        return importDto;
    }
}
