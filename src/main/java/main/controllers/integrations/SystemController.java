package main.controllers.integrations;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.integrations.SystemDao;
import main.model.dto.integrations.SystemDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class SystemController extends BaseController<SystemDto> {

    private final SystemDao systemDao;

    public SystemController(UserDto user) {
        super(user);
        systemDao = new SystemDao();
    }

    @Override
    public List<SystemDto> get(SystemDto entity) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(entity.getProject_id()).isViewer()) {
            return systemDao.searchAll(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view lsit of integrated systems", baseUser);
        }
    }

    @Override
    public SystemDto create(SystemDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return systemDao.create(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add the integration system", baseUser);
        }
    }

    @Override
    public boolean delete(SystemDto entity) throws AqualityException {
        return false;
    }
}
