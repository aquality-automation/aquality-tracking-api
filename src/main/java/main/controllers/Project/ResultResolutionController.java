package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.ResultResolutionDao;
import main.model.dto.ResultResolutionDto;
import main.model.dto.UserDto;

import java.util.List;

public class ResultResolutionController extends BaseController<ResultResolutionDto> {
    private ResultResolutionDao resultResolutionDao;

    public ResultResolutionController(UserDto user) {
        super(user);
        resultResolutionDao = new ResultResolutionDao();
    }

    @Override
    public ResultResolutionDto create(ResultResolutionDto template) throws AqualityException {
        if (baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isAdmin() || baseUser.getProjectUser(template.getProject_id()).isManager()) {
            return resultResolutionDao.create(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Result Resolution", baseUser);
        }
    }

    @Override
    public List<ResultResolutionDto> get(ResultResolutionDto template) throws AqualityException {
        return resultResolutionDao.searchAll(template);
    }

    @Override
    public boolean delete(ResultResolutionDto template) throws AqualityException {
        if (baseUser.isAdmin()) {
            return resultResolutionDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Result Resolution", baseUser);
        }
    }
}
