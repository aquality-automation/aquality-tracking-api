package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.TestDao;
import main.model.db.dao.project.TestResultDao;
import main.model.db.dao.project.TestResultStatDao;
import main.model.dto.*;

import java.util.List;

public class ResultController extends BaseController<TestResultDto> {
    private TestResultDao testResultDao;
    private TestResultStatDao testResultStatDao;
    private TestDao testDao;
    private ProjectUserController projectUserController;
    private ResultResolutionController resultResolutionController;
    private FinalResultController finalResultController;
    private ProjectController projectController;
    private StepController stepController;
    private StepResultController stepResultController;

    public ResultController(UserDto user) {
        super(user);
        testResultDao = new TestResultDao();
        testResultStatDao = new TestResultStatDao();
        projectUserController = new ProjectUserController(user);
        testDao = new TestDao();
        resultResolutionController = new ResultResolutionController(user);
        finalResultController = new FinalResultController(user);
        projectController = new ProjectController(user);
        stepController = new StepController(user);
        stepResultController = new StepResultController(user);
    }

    @Override
    public TestResultDto create(TestResultDto template) throws AqualityException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            TestResultDto testResult = testResultDao.create(template);
            if(projectController.isStepsEnabled(testResult.getProject_id()) && template.getId() == null){
                createPendingStepResults(testResult);
            }
            return testResult;
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Test Result", baseUser);
        }
    }

    @Override
    public List<TestResultDto> get(TestResultDto template) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            if(template.getLimit() == null){
                template.setLimit(0);
            }
            return fillResults(testResultDao.searchAll(template));
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }


    @Override
    public boolean delete(TestResultDto template) throws AqualityException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return testResultDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Test Result", baseUser);
        }
    }

    public boolean updateMultipleTestResults(List<TestResultDto> entities) throws AqualityException {
        if(entities.size() > 0 && (baseUser.isManager() || baseUser.getProjectUser(entities.get(0).getProject_id()).isEditor())){
            return testResultDao.updateMultiply(entities);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to update Test Result", baseUser);
        }
    }

    public List<TestResultStatDto> get(TestResultStatDto template) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return testResultStatDao.searchAll(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Test Result Statistic", baseUser);
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

        if(results.size() > 0){
            List<FinalResultDto> finalResults = finalResultController.get(new FinalResultDto());
            List<ResultResolutionDto> resolutions = resultResolutionController.get(new ResultResolutionDto());
            TestDto testTemplate = new TestDto();
            testTemplate.setProject_id(results.get(0).getProject_id());
            List<TestDto> tests = testDao.searchAll(testTemplate);

            for (TestResultDto result: results){
                fillResult(result, finalResults, resolutions, tests);
            }
        }

        return results;
    }

    private void fillResult(TestResultDto result, List<FinalResultDto> finalResults, List<ResultResolutionDto> resolutions, List<TestDto> tests) throws AqualityException {
        if (projectController.isStepsEnabled(result.getProject_id())) {
            fillResultSteps(result);
        }

        result.setFinal_result(finalResults.stream().filter(x -> x.getId().equals(result.getFinal_result_id())).findFirst().orElse(null));
        result.setTest(tests.stream().filter(x -> x.getId().equals(result.getTest_id())).findFirst().orElse(null));
        result.setTest_resolution(resolutions.stream().filter(x -> x.getId().equals(result.getTest_resolution_id())).findFirst().orElse(null));
        fillResultAssignee(result);
    }

    private void fillResultSteps(TestResultDto result) throws AqualityException {
        StepResultDto stepResultTemplate = new StepResultDto();
        stepResultTemplate.setResult_id(result.getId());
        stepResultTemplate.setProject_id(result.getProject_id());
        result.setSteps(stepResultController.get(stepResultTemplate));
    }

    private void fillResultAssignee(TestResultDto result) throws AqualityException {
        if (result.getAssignee() != null) {
            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setUser_id(result.getAssignee());
            projectUserDto.setProject_id(result.getProject_id());
            result.setAssigned_user(projectUserController.get(projectUserDto).get(0));
        }
    }

}
