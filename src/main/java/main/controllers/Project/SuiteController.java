package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.*;
import main.model.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class SuiteController extends BaseController<TestSuiteDto> {
    private TestSuiteDao testSuiteDao;
    private TestRunDao testRunDao;
    private Test2SuiteDao test2SuiteDao;
    private TestResultDao testResultDao;
    private SuiteStatisticDao suiteStatisticDao;
    private TestController testController;

    public SuiteController(UserDto user) {
        super(user);
        testSuiteDao = new TestSuiteDao();
        testRunDao = new TestRunDao();
        testResultDao = new TestResultDao();
        suiteStatisticDao = new SuiteStatisticDao();
        test2SuiteDao = new Test2SuiteDao();
        testController = new TestController(user);
    }

    @Override
    public TestSuiteDto create(TestSuiteDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return testSuiteDao.create(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Test Suite", baseUser);
        }
    }

    public List<TestSuiteDto> get(TestSuiteDto template, boolean withChildren) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()) {
            return fillTestSuites(testSuiteDao.searchAll(template), withChildren);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Suites", baseUser);
        }
    }

    @Override
    public List<TestSuiteDto> get(TestSuiteDto template) throws AqualityException {
        return get(template, false);
    }

    public TestSuiteDto get(String name, Integer projectId) throws AqualityException {
        TestSuiteDto template = new TestSuiteDto();
        template.setName(name);
        template.setProject_id(projectId);
        try {
            return get(template, false).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new AqualityException("The '%s' suite does not exist.", name);
        }
    }

    @Override
    public boolean delete(TestSuiteDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isManager()) {
            return testSuiteDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete TestSuite", baseUser);
        }
    }

    public List<SuiteStatisticDto> get(SuiteStatisticDto template) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProjectId()).isViewer()) {
            return suiteStatisticDao.searchAll(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Suite Statistic", baseUser);
        }
    }

    public List<TestDto> findLegacyTests(Integer projectId, Integer suiteId, Integer notExecutedFor) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(projectId).isEditor()) {
            TestRunDto testRunTemplate = new TestRunDto();
            testRunTemplate.setTest_suite_id(suiteId);
            testRunTemplate.setLimit(notExecutedFor);
            List<TestRunDto> testRuns = testRunDao.searchAll(testRunTemplate);
            TestDto testTemplate = new TestDto();
            testTemplate.setTest_suite_id(suiteId);
            testTemplate.setProject_id(projectId);
            List<TestDto> tests = testController.get(testTemplate);
            for (TestRunDto testRun : testRuns) {
                TestResultDto testResultTemplate = new TestResultDto();
                testResultTemplate.setTest_run_id(testRun.getId());
                List<TestResultDto> testResults = testResultDao.searchAll(testResultTemplate);
                tests = tests.stream().filter(test -> {
                    TestResultDto currentResult = testResults.stream().filter(result -> result.getTest_id().equals(test.getId())).findFirst().orElse(null);
                    return currentResult == null || currentResult.getFinal_result_id() == 3;
                }).collect(Collectors.toList());
            }

            return tests;
        } else {
            throw new AqualityPermissionsException("Account is not allowed to Sync Test Suite", baseUser);
        }
    }

    public void syncLegacyTests(Integer projectId, List<TestDto> legacyTests, Integer suiteId, boolean removeNotExecutedResults) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(projectId).isEditor()) {
            legacyTests.forEach(test -> {
                Test2SuiteDto test2Suite = new Test2SuiteDto();
                test2Suite.setTest_id(test.getId());
                test2Suite.setSuite_id(suiteId);
                try {
                    test2SuiteDao.delete(test2Suite);
                    if (removeNotExecutedResults) {
                        removePendingResultsForTest(suiteId, test.getId());
                    }
                } catch (AqualityException e) {
                    e.printStackTrace();
                }
            });
        } else {
            throw new AqualityPermissionsException("Account is not allowed to Sync Test Suite", baseUser);
        }
    }

    private List<TestSuiteDto> fillTestSuites(List<TestSuiteDto> testSuites, boolean withChildren) throws AqualityException {
        if (withChildren) {
            for (TestSuiteDto suite : testSuites) {
                TestDto testTemplate = new TestDto();
                testTemplate.setTest_suite_id(suite.getId());
                testTemplate.setProject_id(suite.getProject_id());
                List<TestDto> tests = testController.get(testTemplate, false);
                suite.setTests(tests);
            }
        }
        return testSuites;
    }

    private void removePendingResultsForTest(Integer suiteId, Integer testId) throws AqualityException {
        List<TestResultDto> testResults = testResultDao.selectSuiteLegacyResults(suiteId, testId);
        testResults.forEach(testResult -> {
            try {
                testResultDao.delete(testResult);
            } catch (AqualityException e) {
                e.printStackTrace();
            }
        });
    }
}
