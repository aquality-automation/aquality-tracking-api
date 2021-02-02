package main.controllers.integrations;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.integrations.SystemTypeDao;
import main.model.dto.integrations.types.SystemTypeDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class SystemTypeController extends BaseController<SystemTypeDto> {
    private SystemTypeDao systemTypeDao;

    public SystemTypeController(UserDto user) {
        super(user);
        systemTypeDao = new SystemTypeDao();
    }

    @Override
    public List<SystemTypeDto> get(SystemTypeDto entity) throws AqualityException {
        return systemTypeDao.searchAll(entity);
    }

    @Override
    public SystemTypeDto create(SystemTypeDto entity) throws AqualityException {
        throw getUnsupportedException("CREATE");
    }

    @Override
    public boolean delete(SystemTypeDto entity) throws AqualityException {
        throw getUnsupportedException("DELETE");
    }

    private AqualityException getUnsupportedException(String operationName) {
        return new AqualityException("Operation " + operationName + " is not supported for integration_systems table. This table includes list of constants.");
    }
}
