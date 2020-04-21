package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.Test2SuiteDao;
import main.model.dto.project.Test2SuiteDto;
import main.model.dto.project.TestSuiteDto;
import main.model.dto.settings.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class Test2SuiteController extends BaseController<Test2SuiteDto> {

    private Test2SuiteDao test2SuiteDao;

    public Test2SuiteController(UserDto user) {
        super(user);
        test2SuiteDao = new Test2SuiteDao();
    }

    public Test2SuiteDto create(Test2SuiteDto template, Integer projectId) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(projectId).isEditor()) {
            return test2SuiteDao.create(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Project User", baseUser);
        }
    }

    public boolean delete(Test2SuiteDto template, Integer projectId) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(projectId).isEditor()) {
            return test2SuiteDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test from Suite", baseUser);
        }
    }

    @Override
    public List<Test2SuiteDto> get(Test2SuiteDto template) throws AqualityException {
        return test2SuiteDao.searchAll(template);
    }

    @Override
    public Test2SuiteDto create(Test2SuiteDto entity) throws AqualityException {
        return create(entity, null);
    }

    @Override
    public boolean delete(Test2SuiteDto entity) throws AqualityException {
        return delete(entity, null);
    }

    List<TestSuiteDto> convertToSuites(List<Test2SuiteDto> test2Suites, List<TestSuiteDto> suites) {
        return test2Suites.stream().map(test2suite
                -> suites.stream().filter(x -> x.getId().equals(test2suite.getSuite_id())).findFirst().orElse(null)).collect(Collectors.toList());
    }
}
