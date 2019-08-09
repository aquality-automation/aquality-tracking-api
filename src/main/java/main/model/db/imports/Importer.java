package main.model.db.imports;

import main.exceptions.RPException;
import main.model.dto.ImportDto;
import main.model.dto.TestRunDto;
import main.model.dto.UserDto;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Importer extends BaseImporter {
    private String environment;
    private String build_name;
    private Integer testRunId;
    private List<String> files;
    private String type;
    private TestNameNodeType testNameNodeType;
    private String suiteName;
    private String author;
    private String ci_build;
    private boolean singleTestRun;

    private HandlerFactory handlerFactory = new HandlerFactory();

    public Importer(List<String> files, TestRunDto testRunTemplate, String pattern, String type, TestNameNodeType testNameNodeType, boolean singleTestRun, UserDto user) throws RPException, ParserConfigurationException, SAXException {
        super(testRunTemplate.getProject_id(), pattern, user);
        this.environment = testRunTemplate.getExecution_environment();
        this.ci_build = testRunTemplate.getCi_build();
        this.build_name = testRunTemplate.getBuild_name();
        this.suiteName = testRunTemplate.getTest_suite().getName();
        this.testRunId = testRunTemplate.getId();
        this.files = files;
        this.author = testRunTemplate.getAuthor();
        this.type = type;
        this.testNameNodeType = testNameNodeType;
        this.singleTestRun = singleTestRun;
    }

    public List<ImportDto> executeImport() throws RPException {
        if(testRunId == null && !singleTestRun){
            return parseIntoMultiple();
        }

        return Collections.singletonList(parseIntoOne());
    }

    private ImportDto parseIntoOne() throws RPException {
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

    private List<ImportDto> parseIntoMultiple() throws RPException {
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

    private void executeResultsCreation() throws RPException {
        fillTestRunWithInputData();
        fillTestSuiteWithInputData();
        this.createResults(testRunId != null);
        this.testRun = new TestRunDto();
        this.testResults = new ArrayList<>();
        this.tests = new ArrayList<>();
    }

    private void storeResults(Handler handler) throws RPException {
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
        testRun.setCi_build(this.ci_build);
        if(author != null) this.testRun.setAuthor(author);
        if(environment != null) this.testRun.setExecution_environment(environment);
        if(build_name != null) this.testRun.setBuild_name(build_name);
        if(testRunId != null) this.testRun.setId(testRunId);
    }

    private ImportDto finishImport() throws RPException {
        importDto.setFinished(new Date());
        importDto.setIs_finished(1);
        importDto.addToLog("Import was finished!");
        return importDao.create(importDto);
    }

    private void finishImportWithError(String log) throws RPException {
        if(log == null){
            log = "Without any error message :(";
        }

        importDto.setFinished(new Date());
        importDto.setIs_finished(1);
        importDto.addToLog("Import was finished with Error! " + log);
        importDao.create(importDto);
    }

    private void createImport(String log) throws RPException {
        importDto = new ImportDto();
        importDto.setStarted(new Date());
        importDto.setProject_id(projectId);
        importDto.setIs_finished(0);
        importDto.setLog(log);
        importDto = importDao.create(importDto);
    }
}
