package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.TestResultAttachmentDao;
import main.model.db.dao.project.TestResultDao;
import main.model.db.dao.project.TestResultStatDao;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;

import java.util.ArrayList;
import java.util.List;

public class ResultController extends BaseController<TestResultDto> {
    private final TestResultDao testResultDao;
    private final TestResultStatDao testResultStatDao;
    private final TestResultAttachmentDao testResultAttachmentDao;
    private final TestController testController;
    private final FinalResultController finalResultController;
    private final ProjectController projectController;
    private final StepController stepController;
    private final StepResultController stepResultController;
    private final IssueController issueController;

    public ResultController(UserDto user) {
        super(user);
        testResultDao = new TestResultDao();
        testResultStatDao = new TestResultStatDao();;
        testResultAttachmentDao = new TestResultAttachmentDao();
        testController = new TestController(user);
        finalResultController = new FinalResultController(user);
        projectController = new ProjectController(user);
        stepController = new StepController(user);
        stepResultController = new StepResultController(user);
        issueController = new IssueController(user);
    }

    @Override
    public TestResultDto create(TestResultDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            TestResultDto testResult = testResultDao.create(template);
            if (projectController.isStepsEnabled(testResult.getProject_id()) && template.getId() == null) {
                createPendingStepResults(testResult);
            }
            return testResult;
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Test Result", baseUser);
        }
    }

    @Override
    public List<TestResultDto> get(TestResultDto template) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()) {
            return fillResults(testResultDao.searchAll(template));
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }

    @Override
    public boolean delete(TestResultDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return testResultDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test Result", baseUser);
        }
    }

    public boolean createMultiple(List<TestResultAttachmentDto> listOfAttachments) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(listOfAttachments.get(0).getProject_id()).isEditor()) {
            return testResultAttachmentDao.createMultiply(listOfAttachments);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add Test Result Attachment", baseUser);
        }
    }

    public List<TestResultDto> getLatestResultsByMilestone(Integer projectId, Integer milestoneId) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer()) {
            return fillResults(testResultDao.selectLatestResultsByMilestone(milestoneId));
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }

    public boolean updateMultipleTestResults(List<TestResultDto> entities) throws AqualityException {
        if (entities.size() > 0 && (baseUser.isManager() || baseUser.getProjectUser(entities.get(0).getProject_id()).isEditor())) {
            return testResultDao.updateMultiply(entities);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to update Test Result", baseUser);
        }
    }

    public List<TestResultStatDto> get(TestResultStatDto template) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()) {
            return testResultStatDao.searchAll(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Result Statistic", baseUser);
        }
    }

    public TestResultAttachmentDto create(TestResultAttachmentDto attachment) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(attachment.getProject_id()).isEditor()) {
            return testResultAttachmentDao.create(attachment);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add Test Result Attachment", baseUser);
        }
    }

    public List<TestResultAttachmentDto> get(TestResultAttachmentDto testResultAttachment) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(testResultAttachment.getProject_id()).isViewer()) {
            return testResultAttachmentDao.searchAll(testResultAttachment);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Result Attachment", baseUser);
        }
    }

    public boolean delete(TestResultAttachmentDto attachment) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(attachment.getProject_id()).isEditor()) {
            return testResultAttachmentDao.delete(attachment);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test Result Attachment", baseUser);
        }
    }

    private void createPendingStepResults(TestResultDto template) throws AqualityException {
        Step2TestDto step2TestTemplate = new Step2TestDto();
        step2TestTemplate.setProject_id(template.getProject_id());
        step2TestTemplate.setTest_id(template.getTest_id());
        List<StepDto> testSteps = stepController.getTestSteps(step2TestTemplate);
        for (StepDto step : testSteps) {
            StepResultDto stepResult = new StepResultDto();
            stepResult.setProject_id(template.getProject_id());
            stepResult.setResult_id(template.getId());
            stepResult.setType_id(step.getType_id());
            stepResult.setName(step.getName());
            stepResult.setFinal_result_id(3);
            stepResult.setOrder(step.getOrder());
            stepResultController.create(stepResult);
        }
    }

    private List<TestResultDto> fillResults(List<TestResultDto> results) throws AqualityException {

        if (results.size() > 0) {
            int projectId = results.get(0).getProject_id();
            List<FinalResultDto> finalResults = finalResultController.get(new FinalResultDto());
            IssueDto issueDto = new IssueDto();
            issueDto.setProject_id(projectId);
            List<IssueDto> issues = issueController.get(issueDto);

            TestDto testTemplate = new TestDto();
            testTemplate.setProject_id(projectId);
            List<TestDto> tests = testController.get(testTemplate);

            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(projectId);

            boolean isStepsEnabled = projectController.isStepsEnabled(projectId);

            for (TestResultDto result : results) {
                fillResult(result, finalResults, tests, issues, isStepsEnabled);
            }
        }

        return results;
    }

    private void fillResult(TestResultDto result, List<FinalResultDto> finalResults, List<TestDto> tests, List<IssueDto> issues, boolean isStepsEnabled) throws AqualityException {
        if (isStepsEnabled) {
            fillResultSteps(result);
        }

        result.setFinal_result(finalResults.stream().filter(x -> x.getId().equals(result.getFinal_result_id())).findFirst().orElse(null));
        result.setTest(tests.stream().filter(x -> x.getId().equals(result.getTest_id())).findFirst().orElse(null));
        if(result.getIssue_id() != null) {
            result.setIssue(issues.stream().filter(x -> x.getId().equals(result.getIssue_id())).findFirst().orElse(null));
        }
    }

    private void fillResultSteps(TestResultDto result) throws AqualityException {
        StepResultDto stepResultTemplate = new StepResultDto();
        stepResultTemplate.setResult_id(result.getId());
        stepResultTemplate.setProject_id(result.getProject_id());
        result.setSteps(stepResultController.get(stepResultTemplate));
    }
}
