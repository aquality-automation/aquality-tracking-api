package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.BodyPatternDao;
import main.model.dto.BodyPatternDto;
import main.model.dto.UserDto;

import java.util.List;

public class BodyPatternController extends BaseController<BodyPatternDto> {
    private BodyPatternDao bodyPatternDao;

    public BodyPatternController(UserDto user) {
        super(user);
        bodyPatternDao = new BodyPatternDao();
    }

    @Override
    public BodyPatternDto create(BodyPatternDto template) throws AqualityException {
        if (baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return bodyPatternDao.create(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Body Pattern", baseUser);
        }
    }

    @Override
    public List<BodyPatternDto> get(BodyPatternDto template) throws AqualityException {
        if (baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isViewer()) {
            return bodyPatternDao.searchAll(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Body Patterns", baseUser);
        }
    }

    @Override
    public boolean delete(BodyPatternDto template) throws AqualityException {
        if (baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return bodyPatternDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Body Pattern", baseUser);
        }
    }
}
