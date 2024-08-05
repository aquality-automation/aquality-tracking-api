package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityParametersException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.TestResultAttachmentDao;
import main.model.db.dao.project.TestResultDao;
import main.model.db.dao.project.TestResultStatDao;
import main.model.dto.AttachmentDto;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;
import main.utils.RegexpUtil;

import java.util.*;
import java.util.stream.Collectors;

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
    private final TestResultAttachmentController testResultAttachmentController;
    private final Integer FAILED_STATUS_ID = 2;

    public ResultController(UserDto user) {
        super(user);
        testResultDao = new TestResultDao();
        testResultStatDao = new TestResultStatDao();
        testResultAttachmentDao = new TestResultAttachmentDao();
        testController = new TestController(user);
        finalResultController = new FinalResultController(user);
        projectController = new ProjectController(user);
        stepController = new StepController(user);
        stepResultController = new StepResultController(user);
        issueController = new IssueController(user);
        testResultAttachmentController = new TestResultAttachmentController(user);
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

    public TestResultDto updateWithFinalResultIdAndFailReason(TestResultDto testResult) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(testResult.getProject_id()).isEditor()) {
            return testResultDao.updateFinalResultIdAndFailReason(
                    testResult.getId(),
                    testResult.getFinal_result_id(),
                    testResult.getFail_reason(),
                    testResult.getFinish_date());
        } else {
            throw new AqualityPermissionsException("Account is not allowed to update Test Result", baseUser);
        }
    }

    @Override
    public List<TestResultDto> get(TestResultDto template) throws AqualityException {
        checkReadPermissions(template.getProject_id());
        return fillResults(testResultDao.searchAll(template));
    }

    public List<TestResultDto> getRaw(TestResultDto template) throws AqualityException {
        checkReadPermissions(template.getProject_id());
        return testResultDao.searchAll(template);
    }

    @Override
    public boolean delete(TestResultDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return testResultDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test Result", baseUser);
        }
    }

    public List<TestResultDto> getOnlyFailedResults(TestResultDto testResultTemplate) throws AqualityException {
        List<TestResultDto> testResults = this.get(testResultTemplate);
        return testResults.stream()
                .filter(x -> !Objects.equals(x.getFinal_result_id(), FAILED_STATUS_ID) &&
                        x.getFail_reason() != null &&
                        x.getIssue_id() == null)
                .collect(Collectors.toList());
    }

    public boolean createMultiple(List<TestResultAttachmentDto> listOfAttachments) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(listOfAttachments.get(0).getProject_id()).isEditor()) {
            return testResultAttachmentDao.createMultiply(listOfAttachments);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add Test Result Attachment", baseUser);
        }
    }

    public List<TestResultDto> getLatestResultsByMilestone(Integer projectId, Integer milestoneId)
            throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer()) {
            return fillResults(testResultDao.selectLatestResultsByMilestone(milestoneId));
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }

    public boolean updateMultipleTestResults(List<TestResultDto> entities) throws AqualityException {
        if (!entities.isEmpty()
                && (baseUser.isManager() || baseUser.getProjectUser(entities.get(0).getProject_id()).isEditor())) {
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

    public Map<String, Integer> matchIssues(Integer testResultId) throws AqualityException {
        TestResultDto testResultTemplate = new TestResultDto();
        testResultTemplate.setId(testResultId);
        List<TestResultDto> testResults = this.getOnlyFailedResults(testResultTemplate);
        if (testResults.isEmpty()) {
            throw new AqualityParametersException("No test result found to update. Wrong ID might be provided.");
        }
        IssueDto issueTemplate = new IssueDto();
        issueTemplate.setProject_id(testResults.get(0).getProject_id());
        List<IssueDto> issues = issueController.get(issueTemplate);
        Integer count = assignIssuesToResults(issues, testResults);
        Map<String, Integer> results = new HashMap<>();
        results.put("Issues assigned", count);
        return results;
    }

    public Integer assignIssuesToResults(List<IssueDto> issues, List<TestResultDto> testResults)
            throws AqualityException {
        Integer count = 0;
        for (TestResultDto testResult : testResults) {
            for (IssueDto issue : issues) {
                if (issue.getExpression() != null
                        && RegexpUtil.match(testResult.getFail_reason(), issue.getExpression())) {
                    testResult.setIssue_id(issue.getId());
                    this.create(testResult);
                    count++;
                }
            }
        }
        return count;
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

        if (!results.isEmpty()) {
            long start1 = System.currentTimeMillis();
            int projectId = results.get(0).getProject_id();
            List<FinalResultDto> finalResults = finalResultController.get(new FinalResultDto());

            Map<Integer, FinalResultDto> finalResultsMap = new HashMap<>();
            for(FinalResultDto resultDto : finalResults) {
               finalResultsMap.put(resultDto.getId(), resultDto);
            }

            IssueDto issueDto = new IssueDto();
            issueDto.setProject_id(projectId);
            List<IssueDto> issues = issueController.get(issueDto);

            Map<Integer, IssueDto> issuesMap = new HashMap<>();
            for(IssueDto issueDto2 : issues) {
                issuesMap.put(issueDto2.getId(), issueDto2);
            }


            long start2 = System.currentTimeMillis();
            System.out.println("get issue = " + (start2-start1));
            TestDto testTemplate = new TestDto();
            testTemplate.setProject_id(projectId);
            List<TestDto> tests = testController.get(testTemplate);

            Map<Integer, TestDto> testsMap = new HashMap<>();
            for(TestDto testDto : tests) {
                testsMap.put(testDto.getId(), testDto);
            }



            long start3 = System.currentTimeMillis();
            System.out.println("get test template(project_id) = " + (start3-start2));
            TestResultAttachmentDto testResultAttachmentTemplate = new TestResultAttachmentDto();
            testResultAttachmentTemplate.setProject_id(projectId);
            List<TestResultAttachmentDto> testResultAttachments = testResultAttachmentController
                    .get(testResultAttachmentTemplate);


            Map<Integer, List<TestResultAttachmentDto>> attachmentsMap = new HashMap<>();


            for(TestResultAttachmentDto attachmentDto : testResultAttachments) {
                if(!attachmentsMap.containsKey(attachmentDto.getId())) {
                    List<TestResultAttachmentDto> res = new ArrayList<>();
                    res.add(attachmentDto);
                    attachmentsMap.put(attachmentDto.getTest_result_id(), res);
                }
                else {
                    attachmentsMap.get(attachmentDto.getTest_result_id()).add(attachmentDto);
                }


            }

            long start4 = System.currentTimeMillis();
            System.out.println("get test attachments = " + (start4-start3));
            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(projectId);

            boolean isStepsEnabled = projectController.isStepsEnabled(projectId);

            long start5 = System.currentTimeMillis();
            for (TestResultDto result : results) {
                fillResult(result, finalResultsMap, testsMap, issuesMap, attachmentsMap, isStepsEnabled);
            }
            long start6 = System.currentTimeMillis();
            System.out.println("Exact filling results " + (start6-start5));
            System.out.println("Whole filling function " + (start6-start1));
        }

        return results;
    }

    private void fillResult(TestResultDto result, Map<Integer, FinalResultDto> finalResults, Map<Integer, TestDto> tests,
                            Map<Integer, IssueDto> issues, Map<Integer,List<TestResultAttachmentDto>> attachments, boolean isStepsEnabled)
            throws AqualityException {
        if (isStepsEnabled) {
            fillResultSteps(result);
        }

      //  result.setFinal_result(finalResults.stream().filter(x -> x.getId().equals(result.getFinal_result_id()))
      //          .findFirst().orElse(null));
        result.setFinal_result(finalResults.get(result.getFinal_result_id()));

       // result.setTest(tests.stream().filter(x -> x.getId().equals(result.getTest_id())).findFirst().orElse(null));
        result.setTest(tests.get(result.getTest_id()));

        if (result.getIssue_id() != null) {
            //result.setIssue(
             //       issues.stream().filter(x -> x.getId().equals(result.getIssue_id())).findFirst().orElse(null));
            result.setIssue(issues.get(result.getIssue_id()));
        }
       // result.setAttachments(attachments.stream().filter(x -> x.getTest_result_id().equals(result.getId()))
        //        .collect(Collectors.toList()));
        result.setAttachments(attachments.get(result.getId()));
    }

    private void fillResultSteps(TestResultDto result) throws AqualityException {
        StepResultDto stepResultTemplate = new StepResultDto();
        stepResultTemplate.setResult_id(result.getId());
        stepResultTemplate.setProject_id(result.getProject_id());
        result.setSteps(stepResultController.get(stepResultTemplate));
    }

    private void checkReadPermissions(Integer projectId) throws AqualityException {
        if (!(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer())) {
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }
}
