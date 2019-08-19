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

    public ResultController(UserDto user) {
        super(user);
        testResultDao = new TestResultDao();
        testResultStatDao = new TestResultStatDao();
        projectUserController = new ProjectUserController(user);
        testDao = new TestDao();
        resultResolutionController = new ResultResolutionController(user);
        finalResultController = new FinalResultController(user);
    }

    @Override
    public TestResultDto create(TestResultDto template) throws AqualityException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return testResultDao.create(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Test Result", baseUser);
        }
    }

    public List<TestResultDto> get(TestResultDto template, Integer limit) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            template.setLimit(limit);
            return fillResults(testResultDao.searchAll(template));
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Test Results", baseUser);
        }
    }

    @Override
    public List<TestResultDto> get(TestResultDto template) throws AqualityException {
        return get(template, 0);
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

    //TODO Refactoring
    private List<TestResultDto> fillResults(List<TestResultDto> results) throws AqualityException {

        if(results.size() > 0){
            List<FinalResultDto> finalResults = finalResultController.get(new FinalResultDto());
            List<ResultResolutionDto> resolutions = resultResolutionController.get(new ResultResolutionDto());
            TestDto testTemplate = new TestDto();
            testTemplate.setProject_id(results.get(0).getProject_id());
            List<TestDto> tests = testDao.searchAll(testTemplate);

            for (TestResultDto result: results){

                result.setFinal_result(finalResults.stream().filter( x -> x.getId().equals(result.getFinal_result_id())).findFirst().orElse(null));
                result.setTest(tests.stream().filter(x -> x.getId().equals(result.getTest_id())).findFirst().orElse(null));
                result.setTest_resolution(resolutions.stream().filter( x -> x.getId().equals(result.getTest_resolution_id())).findFirst().orElse(null));

                if(result.getAssignee() != null){
                    ProjectUserDto projectUserDto = new ProjectUserDto();
                    projectUserDto.setUser_id(result.getAssignee());
                    projectUserDto.setProject_id(result.getProject_id());
                    result.setAssigned_user(projectUserController.get(projectUserDto).get(0));
                }
            }
        }

        return results;
    }
}
