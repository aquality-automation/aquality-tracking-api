package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityParametersException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.TestRunDao;
import main.model.db.dao.project.TestRunLabelDao;
import main.model.db.dao.project.TestRunStatisticDao;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunController extends BaseController<TestRunDto> {
    private TestRunDao testRunDao;
    private TestRunLabelDao testRunLabelDao;
    private TestRunStatisticDao testRunStatisticDao;
    private TestController testController;
    private ResultController resultController;
    private SuiteController suiteController;
    private MilestoneController milestoneController;
    private IssueController issueController;

    public TestRunController(UserDto user) {
        super(user);
        testRunDao = new TestRunDao();
        testRunLabelDao = new TestRunLabelDao();
        testRunStatisticDao = new TestRunStatisticDao();
        testController = new TestController(user);
        resultController = new ResultController(user);
        suiteController = new SuiteController(user);
        milestoneController = new MilestoneController(user);
        issueController = new IssueController(user);
    }

    @Override
    public TestRunDto create(TestRunDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            TestRunDto testRun = testRunDao.create(template);
            if (template.getId() == null) {
                createPendingResults(testRun);
            }
            return testRun;
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Test Run", baseUser);
        }
    }

    public List<TestRunDto> get(TestRunDto template, boolean withChildren, Integer limit) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProjectIdById()).isViewer()) {
            template.setLimit(limit);
            return fillTestRuns(testRunDao.searchAll(template), withChildren);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Run", baseUser);
        }
    }

    @Override
    public List<TestRunDto> get(TestRunDto template) throws AqualityException {
        return get(template, false, 0);
    }

    public List<TestRunLabelDto> get(TestRunLabelDto template) throws AqualityException {
        return testRunLabelDao.searchAll(template);
    }

    public List<TestRunStatisticDto> get(TestRunStatisticDto template) throws AqualityException {
        TestRunDto testRunDto = new TestRunDto();
        testRunDto.setId(template.getId());
        Integer projectId = template.getId() != null
                ? testRunDto.getProjectIdById()
                : template.getProject_id();
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer()) {
            return testRunStatisticDao.searchAll(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Run Statistic", baseUser);
        }
    }

    public TestRunDto getLastSuiteTestRun(Integer suiteId, Integer projectId) throws AqualityException {
        TestRunDto template = new TestRunDto();
        template.setTest_suite_id(suiteId);
        template.setProject_id(projectId);
        List<TestRunDto> testRuns = get(template, false, 1);
        if (testRuns.size() > 0) {
            return testRuns.get(0);
        }

        throw new AqualityException("There are no test runs for suite with '%s' id!", suiteId);
    }

    @Override
    public boolean delete(TestRunDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isAdminOrManager()) {
            return testRunDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test Run", baseUser);
        }
    }

    public boolean delete(List<TestRunDto> template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.get(0).getProject_id()).isAdminOrManager()) {
            return testRunDao.deleteMultiply(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test Run", baseUser);
        }
    }

    private void createPendingResults(TestRunDto testRunTemplate) throws AqualityException {
        TestDto testTemplate = new TestDto();
        testTemplate.setTest_suite_id(testRunTemplate.getTest_suite_id());
        testTemplate.setProject_id(testRunTemplate.getProject_id());
        List<TestDto> tests = testController.get(testTemplate);
        for (TestDto test : tests) {
            TestResultDto pendingTestResult = new TestResultDto();
            pendingTestResult.setProject_id(test.getProject_id());
            pendingTestResult.setStart_date(testRunTemplate.getStart_time());
            pendingTestResult.setFinish_date(testRunTemplate.getStart_time());
            pendingTestResult.setTest_id(test.getId());
            pendingTestResult.setTest_run_id(testRunTemplate.getId());
            pendingTestResult.setFinal_result_id(3);
            pendingTestResult.setDebug(testRunTemplate.getDebug());
            resultController.create(pendingTestResult);
        }
    }

    private List<TestRunDto> fillTestRuns(List<TestRunDto> testRuns, boolean withChildren) throws AqualityException {

        if (testRuns.size() > 0) {
            testRuns = fillMilestonesAndSuites(testRuns);
            if (withChildren) {
                testRuns = fillTestRunResults(testRuns);
            }
        }
        return testRuns;
    }

    private List<TestRunDto> fillTestRunResults(List<TestRunDto> testRuns) throws AqualityException {
        for (TestRunDto testRun : testRuns) {
            TestResultDto testResultTemplate = new TestResultDto();
            testResultTemplate.setTest_run_id(testRun.getId());
            testResultTemplate.setProject_id(testRun.getProject_id());
            List<TestResultDto> results = resultController.get(testResultTemplate);
            testRun.setTestResults(results);
        }
        return testRuns;
    }

    private List<TestRunDto> fillMilestonesAndSuites(List<TestRunDto> testRuns) throws AqualityException {
        TestSuiteDto suiteTemplate = new TestSuiteDto();
        suiteTemplate.setProject_id(testRuns.get(0).getProject_id());
        MilestoneDto milestoneTemplate = new MilestoneDto();
        milestoneTemplate.setProject_id(testRuns.get(0).getProject_id());

        List<TestSuiteDto> suites = suiteController.get(suiteTemplate, false);
        List<TestRunLabelDto> labels = get(new TestRunLabelDto());
        List<MilestoneDto> milestones = milestoneController.get(milestoneTemplate);

        for (TestRunDto testRun : testRuns) {
            testRun.setMilestone(milestones.stream().filter(x -> x.getId().equals(testRun.getMilestone_id()))
                    .findFirst().orElse(null));
            testRun.setTest_suite(
                    suites.stream().filter(x -> x.getId().equals(testRun.getTest_suite_id())).findFirst().orElse(null));
            testRun.setLabel(
                    labels.stream().filter(x -> x.getId().equals(testRun.getLabel_id())).findFirst().orElse(null));
        }
        return testRuns;
    }

    public Map<String, Integer> matchIssues(Integer testRunId) throws AqualityException {
        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setId(testRunId);
        List<TestRunDto> testRuns = this.get(testRunTemplate);
        if (testRuns.isEmpty()) {
            throw new AqualityParametersException("No test run found to update. Wrong ID might be provided.");
        }
        TestResultDto testResultTemplate = new TestResultDto();
        testResultTemplate.setTest_run_id(testRuns.get(0).getId());
        testResultTemplate.setProject_id(testRuns.get(0).getProject_id());
        List<TestResultDto> testResults = resultController.getOnlyFailedResults(testResultTemplate);
        if (testResults.isEmpty()) {
            throw new AqualityParametersException("No results found to update.");
        }
        IssueDto issueTemplate = new IssueDto();
        issueTemplate.setProject_id(testRuns.get(0).getProject_id());
        List<IssueDto> issues = issueController.get(issueTemplate);
        Integer count = resultController.assignIssuesToResults(issues, testResults);
        Map<String, Integer> results = new HashMap<>();
        results.put("Issues assigned", count);
        return results;
    }
}
