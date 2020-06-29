package main.model.db.imports;

import main.controllers.ControllerFactory;
import main.controllers.Project.IssueController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.*;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;
import main.utils.RegexpUtil;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class BaseImporter {
    private ControllerFactory controllerFactory;
    private IssueController issueController;
    private String pattern;
    protected File file;
    private List<IssueDto> issues;

    BaseImporter(int projectId, String pattern, UserDto user) throws AqualityException {
        this.projectId = projectId;
        this.pattern = pattern;
        controllerFactory = new ControllerFactory(user);
        IssueDto issueTemplate = new IssueDto();
        issueTemplate.setProject_id(this.projectId);
        issueController = controllerFactory.getHandler(issueTemplate);
        issues = issueController.get(issueTemplate);
    }

    private List<TestResultDto> existingResults = new ArrayList<>();
    private ProjectDao projectDao = new ProjectDao();
    private TestResultDao testResultDao = new TestResultDao();
    private TestDao testDao = new TestDao();
    private IssueDao issueDao = new IssueDao();
    protected int projectId;
    TestRunDto testRun;
    TestSuiteDto testSuite;
    List<TestDto> tests = new ArrayList<>();
    List<TestResultDto> testResults = new ArrayList<>();
    ImportDao importDao = new ImportDao();
    ImportDto importDto = new ImportDto();

    void processImport(boolean update) throws AqualityException {
        createTestSuite();
        createTests();
        createTestRun();
        createResults(update);
    }

    private void createResults(boolean update) throws AqualityException {
        existingResults = getExistingResults();

        logToImport("Starting test results creation.");

        for (int i = 0; i < testResults.size(); i++) {
            TestResultDto result = testResults.get(i);

            if(result.getInternalTestId() != null){
                result.setTest(tests.stream().filter(x-> Objects.equals(x.getInternalId(), result.getInternalTestId())).findFirst().orElse(null));
            }else{
                result.setTest(tests.get(i));
            }

            result.setDebug(testRun.getDebug());
            result.setTest_id(result.getTest().getId());
            result.setTest_run_id(testRun.getId());
            result.setFinal_result_updated(result.getFinish_date());
            result.setProject_id(testRun.getProject_id());

            createResult(result, update);
        }

        logToImport("Test results were created.");
    }

    private List<TestResultDto> getExistingResults() throws AqualityException {
        TestResultDto template = new TestResultDto();
        template.setTest_run_id(testRun.getId());
        template.setProject_id(testRun.getProject_id());
        template.setLimit(10000);
        return testResultDao.searchAll(template);
    }

    private void updateImportTestRun() throws AqualityException {
        importDto.setTestrun_id(testRun.getId());
        importDto = importDao.create(importDto);
    }

    void logToImport(String log) throws AqualityException {
        importDto.setProject_id(this.projectId);
        importDto.addToLog(log);
        importDto = importDao.create(importDto);
    }

    private void createTestRun() throws AqualityException {
        if(testRun.getId() != null){
            TestRunDto template = new TestRunDto();
            template.setProject_id(testRun.getProject_id());
            template.setId(testRun.getId());
            List<TestRunDto> testRuns = controllerFactory.getHandler(template).get(template, false, 1);
            if(testRuns.size() > 0){
                testRun = testRuns.get(0);
            } else {
                throw new AqualityException("Test run with %s id does not exist!", testRun.getId());
            }
        }
        else{
            setTestRunStartDate();
            setTestRunFinishDate();
            testRun.setTest_suite_id(testSuite.getId());
            testRun.setId(controllerFactory.getHandler(testRun).create(testRun).getId());
        }
        updateImportTestRun();
        logToImport("Test Run is updated.");
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

        logToImport("Suites are updated.");
    }

    private void createTests() throws AqualityException {
        TestDto testTemplate = new TestDto();
        testTemplate.setProject_id(this.projectId);
        List<TestDto> allTests = testDao.searchAll(testTemplate);
        List<TestDto> completedTests = new ArrayList<>();

        for (TestDto test : this.tests) {
            try {
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
            } catch (AqualityException exception) {
                logToImport(String.format("Was not able to create or update test:\n%s\nCreation was failed with error:\n%s", test.getName(), exception.getMessage()));
            }
        }
        this.tests = completedTests;

        logToImport("Tests are updated.");
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
        controllerFactory.getHandler(test2SuiteDto).create(test2SuiteDto, test.getProject_id());
    }

    private void createResult(TestResultDto result, boolean update) throws AqualityException {
        try{
            TestResultDto existingResult = existingResults.stream().filter(x -> x.getTest_id().equals(result.getTest_id())).findFirst().orElse(null);

            if (existingResult != null) {
                if(existingResult.getPending() > 0 || update){
                    result.setId(existingResult.getId());
                    if(result.getFinal_result_id() == 2){
                        result.setLog("$blank");
                        result.setFail_reason("$blank");
                    }
                    existingResult.setPending(0);
                }
            }

            if(result.getFail_reason() != null && !result.getFail_reason().equals("")){
                predictResultResolution(result);
            }
            controllerFactory.getHandler(result).create(result);
        } catch (AqualityException e){
            logToImport(
                    String.format("Failed on Result Creation for test id:\n%s\nCreation was failed with error:\n%s",
                    result.getTest_id(),
                    e.getMessage()));
        }
    }

    private void predictResultResolution(TestResultDto result) throws AqualityException {
        if(!tryFillByIssue(result, issues)) {
            updateResultWithSimilarError(result);
        }
    }

    private void updateResultWithSimilarError(TestResultDto result) throws AqualityException {
        try{
            ProjectDto project = new ProjectDto();
            project.setId(result.getProject_id());
            project = projectDao.getEntityById(project.getId());

            TestResultDto testResultTemplate = new TestResultDto();
            testResultTemplate.setProject_id(result.getProject_id());
            testResultTemplate.setTest_id(result.getTest().getId());
            testResultTemplate.setLimit(10000);
            List<TestResultDto> testResults = testResultDao.searchAll(testResultTemplate);
            if(testResults.size() > 0){
                TestResultDto similarResult = null;
                if(project.getCompare_result_pattern() != null) {
                    similarResult = compareByRegexp(result, testResults, project.getCompare_result_pattern());
                }
                if(similarResult == null){
                    similarResult = testResults.stream().filter(x -> x.getFail_reason() != null && x.getFail_reason().equals(result.getFail_reason())).findFirst().orElse(null);
                }
                if(similarResult != null && similarResult.getIssue_id() != null){
                    IssueDto issue = issueDao.getEntityById(similarResult.getIssue_id());
                    if(issue.getStatus_id() != 4){
                        result.setIssue_id(similarResult.getIssue_id());
                    }
                }
            }
        } catch (Exception e){
            logToImport(
                    String.format("Failed on predicting fail reason for test id:\n%s\nPrediction was failed with error:\n%s",
                            result.getTest_id(),
                            e.getMessage()));
        }
    }

    private TestResultDto compareByRegexp(TestResultDto result, List<TestResultDto> oldResults, String expression) {
        for (TestResultDto oldResult : oldResults) {
            if(oldResult.getFail_reason() != null && oldResult.getIssue_id() != null ){
                if(RegexpUtil.compareByRegexpGroups(result.getFail_reason(), oldResult.getFail_reason(), expression)){
                    return oldResult;
                }
            }
        }
        return null;
    }

    private boolean tryFillByIssue(TestResultDto result, List<IssueDto> issues) {
        if (result.getFail_reason() != null) {
            for (IssueDto issue : issues) {
                try {
                    if (issue.getExpression() != null && !issue.getStatus_id().equals(4) && RegexpUtil.match(result.getFail_reason(), issue.getExpression())) {
                        result.setIssue_id(issue.getId());
                        return true;
                    }
                } catch (PatternSyntaxException regexException) {
                    issue.setExpression("$blank");
                    try {
                        issueController.create(issue);
                    } catch (AqualityException controllerException) {
                        System.out.println(String.format("Was not able to fix invalid issue expression: id: %s", issue.getId()));
                    }
                }
            }
        }

        return false;
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


