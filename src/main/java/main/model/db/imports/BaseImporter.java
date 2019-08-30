package main.model.db.imports;

import main.controllers.ControllerFactory;
import main.exceptions.AqualityException;
import main.model.db.dao.project.ImportDao;
import main.model.db.dao.project.TestDao;
import main.model.db.dao.project.TestResultDao;
import main.model.dto.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BaseImporter {
    private ControllerFactory controllerFactory;
    private String pattern;
    protected File file;

    BaseImporter(int projectId, String pattern, UserDto user){
        this.projectId = projectId;
        this.pattern = pattern;
        controllerFactory = new ControllerFactory(user);
    }

    private List<TestResultDto> existingResults = new ArrayList<>();
    private TestResultDao testResultDao = new TestResultDao();
    private TestDao testDao = new TestDao();
    protected int projectId;
    TestRunDto testRun;
    TestSuiteDto testSuite;
    List<TestDto> tests = new ArrayList<>();
    List<TestResultDto> testResults = new ArrayList<>();
    ImportDao importDao = new ImportDao();
    ImportDto importDto = new ImportDto();

    void createResults(boolean update) throws AqualityException {
        createTestSuite();
        addLogToImport("Suites are updated.");
        createTests();
        addLogToImport("Tests are updated.");
        createTestRun();
        addLogToImport("Test Run is updated.");
        updateImportTestRun();

        TestResultDto template = new TestResultDto();
        template.setTest_run_id(testRun.getId());
        template.setProject_id(testRun.getProject_id());
        template.setLimit(10000);
        existingResults = testResultDao.searchAll(template);

        int testRunId = testRun.getId();
        addLogToImport("Starting test results creation.");
        for (int i = 0; i < testResults.size(); i++) {
            TestResultDto result = testResults.get(i);
            if(result.getInternalTestId() != null){
                result.setTest(tests.stream().filter(x-> Objects.equals(x.getInternalId(), result.getInternalTestId())).findFirst().orElse(null));
            }else{
                result.setTest(tests.get(i));
            }
            result.setTest_id(result.getTest().getId());
            result.setTest_run_id(testRunId);
            result.setFinal_result_updated(result.getFinish_date());
            result.setProject_id(testRun.getProject_id());

            createResult(result, update);
        }
        addLogToImport("Test results were created.");
    }

    private void updateImportTestRun() throws AqualityException {
        importDto.setTestrun_id(testRun.getId());
        importDto = importDao.create(importDto);
    }

    void addLogToImport(String log) throws AqualityException {
        importDto.addToLog(log);
        importDto = importDao.create(importDto);
    }

    private void createTestRun() throws AqualityException {
        if(testRun.getId() != null){
            this.testRun = controllerFactory.getHandler(testRun).get(testRun, false, 1).get(0);
        }
        else{
            createTestRun((testRun.getBuild_name() != null && !testRun.getBuild_name().equals(""))
                    ? testRun.getBuild_name()
                    : file.getName().substring(0, file.getName().lastIndexOf(".")));
        }
    }

    private void createTestRun(String buildName) throws AqualityException {
        testRun.setBuild_name(buildName);
        setTestRunStartDate();
        setTestRunFinishDate();
        testRun.setTest_suite_id(testSuite.getId());
        testRun.setId(controllerFactory.getHandler(testRun).create(testRun).getId());
    }

    private void setTestRunStartDate(){
        Comparator<TestResultDto> startDate = Comparator.comparing(TestResultDto::getStart_date);
        if(testRun.getStart_time() == null){
            Optional<TestResultDto> min = testResults.stream().min(startDate);
            min.ifPresent(testResultDto -> testRun.setStart_time(testResultDto.getStart_date()));
        }
    }

    private void setTestRunFinishDate(){
        Comparator<TestResultDto> finishDate = Comparator.comparing(TestResultDto::getFinish_date);
        if(testRun.getFinish_time() == null){
            Optional<TestResultDto> max = testResults.stream().max(finishDate);
            max.ifPresent(testResultDto -> testRun.setFinish_time(testResultDto.getFinish_date()));
        }
    }

    private void createTestSuite() throws AqualityException {
        testSuite.setProject_id(projectId);
        List<TestSuiteDto> testSuites = controllerFactory.getHandler(testSuite).get(testSuite, false);

        if (testSuites.size() > 0) {
            testSuite = testSuites.get(0);
        } else {
            testSuite.setId(controllerFactory.getHandler(testSuite).create(testSuite).getId());
        }
    }

    private void createTests() throws AqualityException {
        TestDto testTemplate = new TestDto();
        testTemplate.setProject_id(this.projectId);
        List<TestDto> allTests = testDao.searchAll(testTemplate);
        List<TestDto> completedTests = new ArrayList<>();

        for (TestDto test : this.tests) {
            test.setProject_id(this.projectId);
            TestDto existingTest = tryGetExistingTest(allTests, test);

            if (existingTest != null) {
                test.setId(existingTest.getId());
            } else {
                allTests.add(test);
            }

            test.setId(controllerFactory.getHandler(test).create(test, false).getId());
            linkTestToSuite(test);

            completedTests.add(test);
        }
        this.tests = completedTests;
    }

    private TestDto tryGetExistingTest(List<TestDto> allTests, TestDto test) throws AqualityException {
        if (pattern != null && !pattern.equals("")) {
            return getTestByPatternOrName(allTests, test);
        }
        return getTestByName(allTests, test);
    }

    private void linkTestToSuite(TestDto test) throws AqualityException {
        Test2SuiteDto test2SuiteDto = new Test2SuiteDto();
        test2SuiteDto.setTest_id(test.getId());
        test2SuiteDto.setSuite_id(testSuite.getId());
        controllerFactory.getHandler(test2SuiteDto).create(test2SuiteDto);
    }

    private void createResult(TestResultDto result, boolean update) throws AqualityException {
        try{
            TestResultDto existingResult = existingResults.stream().filter(x -> x.getTest_id().equals(result.getTest_id())).findFirst().orElse(null);

            if (existingResult != null) {
                if(existingResult.getPending() > 0 || update){
                    result.setId(existingResult.getId());
                    if(!Objects.equals(result.getFail_reason(), existingResult.getFail_reason())){
                        result.setComment("$blank");
                    }
                    if(result.getFinal_result_id() == 2){
                        result.setLog("$blank");
                        result.setFail_reason("$blank");
                    }
                    existingResult.setPending(0);
                }
            }

            if(result.getFail_reason() != null && !result.getFail_reason().equals("")){
                updateResultWithSimilarError(result);
            }
            controllerFactory.getHandler(result).create(result);
        } catch (AqualityException e){
            throw e;
        } catch (Exception e){
            throw new AqualityException("Failed on Result Creation for test id: " + result.getTest_id());
        }
    }

    private TestResultDto updateResultWithSimilarError(TestResultDto result) throws AqualityException {
        try{
            TestResultDto testResultTemplate = new TestResultDto();
            testResultTemplate.setProject_id(result.getProject_id());
            testResultTemplate.setTest_id(result.getTest().getId());
            testResultTemplate.setLimit(10000);
            List<TestResultDto> testResults = testResultDao.searchAll(testResultTemplate);
            if(testResults.size() > 0){
                TestResultDto similarResult = testResults.stream().filter(x -> x.getFail_reason() != null && x.getFail_reason().equals(result.getFail_reason())).findFirst().orElse(null);
                if(similarResult != null){
                    result.setComment(similarResult.getComment());
                    result.setTest_resolution_id(similarResult.getTest_resolution_id());
                    result.setAssignee(similarResult.getAssignee());
                }
            }

            return result;
        } catch (Exception e){
            throw new AqualityException("Failed on update Result with similar error");
        }
    }

    private TestDto getTestByPatternOrName(List<TestDto> tests, TestDto importTest) throws AqualityException {
        Pattern pattern = Pattern.compile(this.pattern);
        Matcher matcher = pattern.matcher(importTest.getBody());

        if (matcher.find()) {
            for (TestDto test : tests) {
                String body = test.getBody();
                String stringToMatch = matcher.group(0);
                if (body.contains(stringToMatch)) {
                    return test;
                }
            }
        } else {
            throw new AqualityException("You are trying to import test without uniq identification! Test Name: " + importTest.getName());
        }
        return getTestByName(tests, importTest);
    }

    private TestDto getTestByName(List<TestDto> tests, TestDto importTest){
        return tests.stream().filter(x -> x.getName().trim().equalsIgnoreCase(importTest.getName().trim())).findFirst().orElse(null);
    }
}


