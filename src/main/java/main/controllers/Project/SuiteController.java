package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.SuiteStatisticDao;
import main.model.db.dao.project.TestSuiteDao;
import main.model.dto.SuiteStatisticDto;
import main.model.dto.TestDto;
import main.model.dto.TestSuiteDto;
import main.model.dto.UserDto;

import java.util.List;

public class SuiteController extends BaseController<TestSuiteDto> {
    private TestSuiteDao testSuiteDao;
    private SuiteStatisticDao suiteStatisticDao;
    private TestController testController;

    public SuiteController(UserDto user) {
        super(user);
        testSuiteDao = new TestSuiteDao();
        suiteStatisticDao = new SuiteStatisticDao();
        testController = new TestController(user);
    }

    @Override
    public TestSuiteDto create(TestSuiteDto template) throws RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return testSuiteDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Test Suite", baseUser);
        }
    }

    public List<TestSuiteDto> get(TestSuiteDto template, boolean withChildren) throws  RPException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return fillTestSuites(testSuiteDao.searchAll(template), withChildren);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Test Suites", baseUser);
        }
    }

    @Override
    public List<TestSuiteDto> get(TestSuiteDto template) throws  RPException {
        return get(template, false);
    }

    @Override
    public boolean delete(TestSuiteDto template) throws  RPException {
        if(baseUser.isManager() || baseUser.getProjectUserBySuiteId(template.getId()).isManager()){
            return testSuiteDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete TestSuite", baseUser);
        }
    }

    public List<SuiteStatisticDto> get(SuiteStatisticDto template) throws RPException{
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProjectId()).isViewer()){
            return suiteStatisticDao.searchAll(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Suite Statistic", baseUser);
        }
    }

    private List<TestSuiteDto> fillTestSuites(List<TestSuiteDto> testSuites, boolean withChildren) throws RPException {
        if(withChildren){
            for (TestSuiteDto suite: testSuites){
                TestDto testTemplate = new TestDto();
                testTemplate.setTest_suite_id(suite.getId());
                testTemplate.setProject_id(suite.getProject_id());
                List<TestDto> tests = testController.get(testTemplate, false);
                suite.setTests(tests);
            }
        }
        return testSuites;
    }
}
