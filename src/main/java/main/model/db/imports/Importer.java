package main.model.db.imports;

import lombok.SneakyThrows;
import main.exceptions.AqualityException;
import main.model.dto.project.ImportDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.settings.UserDto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Importer extends BaseImporter {
    private static final Logger log = Logger.getLogger(Importer.class.getName());
    private static final boolean IMPORTS_DEBUG = "true".equalsIgnoreCase(System.getProperty("imports.debug", java.util.Optional.ofNullable(System.getenv("IMPORTS_DEBUG")).orElse("false")));
    private List<String> files;
    private String type;
    private TestNameNodeType testNameNodeType;
    private String suiteName;
    private TestRunDto testRunTemplate;
    private boolean singleTestRun;
    private Date nextFinishTime = new Date();

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

    public List<ImportDto> executeImport() throws AqualityException {
        if(testRunTemplate.getId() == null && !singleTestRun){
            List<ImportDto> multiTestRun = executeMultiTestRunImport();
            return multiTestRun;
        }
        List<ImportDto> singleTestRun = Collections.singletonList(executeSingleTestRunImport());

        return singleTestRun;
    }

    @SneakyThrows
    private ImportDto executeSingleTestRunImport() throws AqualityException {
        try {
            if (IMPORTS_DEBUG && log.isLoggable(Level.FINE)) { log.fine("Starting single test run import: files=" + (files == null ? "[]" : files.toString()) + " suite=" + suiteName + " testRunId=" + testRunTemplate.getId()); }
            createImport("Import into One Test Run was started!");
            readData(this.files);
            executeResultsCreation();
            return finishImport();
        } catch (Exception e) {
            finishImportWithError(e.getMessage());
            throw e;
        }
    }

    @SneakyThrows
    private List<ImportDto> executeMultiTestRunImport() throws AqualityException {
        List<ImportDto> imports = new ArrayList<>();
        for (String pathToFile : this.files) {
            try{
                if (pathToFile == null || pathToFile.trim().isEmpty()) {
                    // defensive: skip empty entries that might appear due to multipart parsing issues
                    log.info("Skipping empty file path in multi-import");
                    continue;
                }
                File file = new File(pathToFile);
                if (!file.exists()) {
                    log.info("Skipping non-existing file: " + pathToFile);
                    continue;
                }
                if (file.length() == 0) {
                    log.info("Skipping empty file on disk (length=0): " + pathToFile);
                    continue;
                }
                if (IMPORTS_DEBUG && log.isLoggable(Level.FINE)) { log.fine("Starting file import: file=" + file.getAbsolutePath() + " suite=" + suiteName); }
                createImport("Import was started for file: " + file.getName());
                readData(file);
                executeResultsCreation();
                imports.add(finishImport());
            } catch (Exception e){
                finishImportWithError(e.getMessage());
                throw e;
            }
        }

        return imports;
    }

    private void executeResultsCreation() throws AqualityException, IOException, URISyntaxException {
        this.processImport(testRunTemplate.getId() != null);
        this.testRun = new TestRunDto();
        this.testResults = new ArrayList<>();
        this.tests = new ArrayList<>();
    }


    private void readData(List<String> filePaths) throws AqualityException {
        for (String pathToFile : filePaths) {
            if (pathToFile == null || pathToFile.trim().isEmpty()) {
                log.info("Skipping empty file path in readData");
                continue;
            }
            File f = new File(pathToFile);
            if (!f.exists()) {
                log.info("Skipping non-existing file in readData: " + pathToFile);
                continue;
            }
            Handler handler = handlerFactory.getHandler(f, type, testNameNodeType, nextFinishTime);
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
        if(this.testRun != null){

            if(this.testRun.getStart_time().before(handlerTestRun.getStart_time())){
                handlerTestRun.setStart_time(this.testRun.getStart_time());
            }

            if(this.testRun.getFinish_time().after(handlerTestRun.getFinish_time())){
                handlerTestRun.setFinish_time(this.testRun.getFinish_time());
            }
        }

        handler.setTestRun(handlerTestRun);
        nextFinishTime = handlerTestRun.getStart_time();
    }

    private void fillTestSuiteWithInputData(){
        testSuite.setName(suiteName);
    }

    private void fillTestRunWithInputData(){
        fillTestRunWithInputData(null);
    }

    private void fillTestRunWithInputData(String fileName){
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
            ImportDto result = importDao.create(importDto);
            if (IMPORTS_DEBUG && log.isLoggable(Level.FINE)) {
                int resultsCount = this.testResults == null ? 0 : this.testResults.size();
                int testsCount = this.tests == null ? 0 : this.tests.size();
                log.fine("Finished import: id=" + result.getId() + " projectId=" + projectId + " suite=" + suiteName + " testsCount=" + testsCount + " resultsCount=" + resultsCount + " files=" + (files == null ? "[]" : files.toString()));
            }
            return result;
        }

    private void finishImportWithError(String log) throws AqualityException {
        if(log == null){
            log = "Without any error message :(";
        }

        importDto.setProject_id(this.projectId);
        importDto.setFinished(new Date());
        importDto.setFinish_status(2);
        importDto.addToLog("Import was finished with Error! " + log);
        importDao.create(importDto);
    }

    private void createImport(String message) throws AqualityException {
        importDto = new ImportDto();
        importDto.setStarted(new Date());
        importDto.setProject_id(projectId);
        importDto.setFinish_status(0);
            importDto.setLog(message);
        importDto = importDao.create(importDto);
            if (IMPORTS_DEBUG && log.isLoggable(Level.FINE)) {
                log.fine("Created import: id=" + importDto.getId() + " projectId=" + projectId + " suite=" + suiteName + " message=" + message + " files=" + (files == null ? "[]" : files.toString()));
            }
        }
}
