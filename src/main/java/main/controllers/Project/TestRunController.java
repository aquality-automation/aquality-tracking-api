package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.TestRunDao;
import main.model.db.dao.project.TestRunLabelDao;
import main.model.db.dao.project.TestRunStatisticDao;
import main.model.dto.*;

import java.util.List;

public class TestRunController extends BaseController<TestRunDto> {
    private TestRunDao testRunDao;
    private TestRunLabelDao testRunLabelDao;
    private TestRunStatisticDao testRunStatisticDao;
    private TestController testController;
    private ResultController resultController;
    private SuiteController suiteController;
    private MilestoneController milestoneController;

    public TestRunController(UserDto user) {
        super(user);
        testRunDao = new TestRunDao();
        testRunLabelDao = new TestRunLabelDao();
        testRunStatisticDao = new TestRunStatisticDao();
        testController = new TestController(user);
        resultController = new ResultController(user);
        suiteController = new SuiteController(user);
        milestoneController = new MilestoneController(user);
    }

    @Override
    public TestRunDto create(TestRunDto template) throws AqualityException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            TestRunDto testRun = testRunDao.create(template);
            if(template.getId() == null){
                createPendingResults(testRun);
            }
            return testRun;
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Test Run", baseUser);
        }
    }

    public List<TestRunDto> get(TestRunDto template, boolean withChildren, Integer limit) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProjectIdById()).isViewer()){
            template.setLimit(limit);
            return fillTestRuns(testRunDao.searchAll(template), withChildren);
        }else{
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
        return testRunStatisticDao.searchAll(template);
    }

    @Override
    public boolean delete(TestRunDto template) throws AqualityException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return testRunDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Test Run", baseUser);
        }
    }

    private void createPendingResults(TestRunDto testRunTemplate) throws AqualityException {
        TestDto testTemplate = new TestDto();
        testTemplate.setTest_suite_id(testRunTemplate.getTest_suite_id());
        testTemplate.setProject_id(testRunTemplate.getProject_id());
        List<TestDto> tests = testController.get(testTemplate, false);
        for (TestDto test: tests) {
            TestResultDto pendingTestResult = new TestResultDto();
            pendingTestResult.setProject_id(test.getProject_id());
            pendingTestResult.setStart_date(testRunTemplate.getStart_time());
            pendingTestResult.setFinish_date(testRunTemplate.getStart_time());
            pendingTestResult.setTest_id(test.getId());
            pendingTestResult.setTest_run_id(testRunTemplate.getId());
            pendingTestResult.setFinal_result_id(3);
            pendingTestResult.setTest_resolution_id(1);
            pendingTestResult.setDebug(testRunTemplate.getDebug());
            resultController.create(pendingTestResult);
        }
    }

    private List<TestRunDto> fillTestRuns(List<TestRunDto> testRuns, boolean withChildren) throws AqualityException {

        if(testRuns.size() > 0){
            testRuns = fillMilestonesAndSuites(testRuns);

            if(withChildren){
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
            List<TestResultDto> results = resultController.get(testResultTemplate, 10000);
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
            testRun.setMilestone(milestones.stream().filter(x -> x.getId().equals(testRun.getMilestone_id())).findFirst().orElse(null));
            testRun.setTest_suite(suites.stream().filter(x -> x.getId().equals(testRun.getTest_suite_id())).findFirst().orElse(null));
            testRun.setLabel(labels.stream().filter(x -> x.getId().equals(testRun.getLabel_id())).findFirst().orElse(null));
        }
        return testRuns;
    }
}
