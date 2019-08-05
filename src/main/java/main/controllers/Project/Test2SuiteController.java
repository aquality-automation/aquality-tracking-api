package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.Test2SuiteDao;
import main.model.dto.Test2SuiteDto;
import main.model.dto.TestSuiteDto;
import main.model.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class Test2SuiteController extends BaseController<Test2SuiteDto> {

    private Test2SuiteDao test2SuiteDao;

    public Test2SuiteController(UserDto user) {
        super(user);
        test2SuiteDao = new Test2SuiteDao();
    }

    @Override
    public Test2SuiteDto create(Test2SuiteDto template) throws RPException {
        if(baseUser.isManager() || baseUser.getProjectUserBySuiteId(template.getSuite_id()).isEditor()){
            return test2SuiteDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Project User", baseUser);
        }
    }

    @Override
    public boolean delete(Test2SuiteDto template) throws  RPException {
        if(baseUser.isManager() || baseUser.getProjectUserBySuiteId(template.getSuite_id()).isEditor()){
            return test2SuiteDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Test from Suite", baseUser);
        }
    }

    @Override
    public List<Test2SuiteDto> get(Test2SuiteDto template) throws  RPException {
        return test2SuiteDao.searchAll(template);
    }

    //TODO Refactoring
    public List<TestSuiteDto> convertToSuites(List<Test2SuiteDto> test2Suites, List<TestSuiteDto> suites) throws RPException {
        return test2Suites.stream().map(test2suite
                -> suites.stream().filter(x -> x.getId().equals(test2suite.getSuite_id())).findFirst().orElse(null)).collect(Collectors.toList());
    }
}
