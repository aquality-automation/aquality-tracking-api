package main.model.db.imports;

import main.exceptions.AqualityException;
import main.model.dto.ImportDto;
import main.model.dto.TestRunDto;
import main.model.dto.UserDto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Importer extends BaseImporter {
    private List<String> files;
    private String type;
    private TestNameNodeType testNameNodeType;
    private String suiteName;
    private TestRunDto testRunTemplate;
    private boolean singleTestRun;

    private HandlerFactory handlerFactory = new HandlerFactory();

    public Importer(List<String> files, TestRunDto testRunTemplate, String pattern, String type, TestNameNodeType testNameNodeType, boolean singleTestRun, UserDto user) {
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
            return parseIntoMultiple();
        }

        return Collections.singletonList(parseIntoOne());
    }

    private ImportDto parseIntoOne() throws AqualityException {
        try {
            createImport("Import into One Test Run was started!");
            for (String pathToFile : this.files) {
                this.file = new File(pathToFile);
                Handler handler = handlerFactory.getHandler(this.file, type, testNameNodeType);
                storeResults(handler);
            }
            executeResultsCreation();
            return finishImport();
        } catch (Exception e) {
            finishImportWithError(e.getMessage());
            throw e;
        }
    }

    private List<ImportDto> parseIntoMultiple() throws AqualityException {
        List<ImportDto> imports = new ArrayList<>();
        for (String pathToFile : this.files) {
            try{
                this.file = new File(pathToFile);
                createImport("Import was started for file: " + this.file.getName());
                Handler handler = handlerFactory.getHandler(this.file, type, testNameNodeType);
                storeResults(handler);
                executeResultsCreation();
                imports.add(finishImport());
            } catch (Exception e){
                finishImportWithError(e.getMessage());
                throw e;
            }
        }

        return imports;
    }

    private void executeResultsCreation() throws AqualityException {
        fillTestRunWithInputData();
        fillTestSuiteWithInputData();
        this.createResults(testRunTemplate.getId() != null);
        this.testRun = new TestRunDto();
        this.testResults = new ArrayList<>();
        this.tests = new ArrayList<>();
    }

    private void storeResults(Handler handler) throws AqualityException {
        this.testRun = handler.getTestRun();
        this.testResults.addAll(handler.getTestResults());
        this.tests.addAll(handler.getTests());
        this.testSuite = handler.getTestSuite();
        addLogToImport("File was parsed correctly!");
    }

    private void fillTestSuiteWithInputData(){
        testSuite.setName(suiteName);
    }

    private void fillTestRunWithInputData(){
        testRun.setProject_id(this.projectId);
        testRun.setCi_build(testRunTemplate.getCi_build());
        this.testRun.setAuthor(testRunTemplate.getAuthor());
        this.testRun.setExecution_environment(testRunTemplate.getExecution_environment());
        this.testRun.setBuild_name(testRunTemplate.getBuild_name());
        this.testRun.setId(testRunTemplate.getId());
        this.testRun.setDebug(testRunTemplate.getDebug());
    }

    private ImportDto finishImport() throws AqualityException {
        importDto.setFinished(new Date());
        importDto.setIs_finished(1);
        importDto.addToLog("Import was finished!");
        return importDao.create(importDto);
    }

    private void finishImportWithError(String log) throws AqualityException {
        if(log == null){
            log = "Without any error message :(";
        }

        importDto.setFinished(new Date());
        importDto.setIs_finished(1);
        importDto.addToLog("Import was finished with Error! " + log);
        importDao.create(importDto);
    }

    private void createImport(String log) throws AqualityException {
        importDto = new ImportDto();
        importDto.setStarted(new Date());
        importDto.setProject_id(projectId);
        importDto.setIs_finished(0);
        importDto.setLog(log);
        importDto = importDao.create(importDto);
    }
}
