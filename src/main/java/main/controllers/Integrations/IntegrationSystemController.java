package main.controllers.Integrations;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.integrations.IntegrationSystemDao;
import main.model.dto.integrations.IntegrationSystemDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class IntegrationSystemController extends BaseController<IntegrationSystemDto> {
    private IntegrationSystemDao integrationSystemDao;

    public IntegrationSystemController(UserDto user) {
        super(user);
        integrationSystemDao = new IntegrationSystemDao();
    }

    @Override
    public List<IntegrationSystemDto> get(IntegrationSystemDto entity) throws AqualityException {
        return integrationSystemDao.searchAll(entity);
    }

    @Override
    public IntegrationSystemDto create(IntegrationSystemDto entity) throws AqualityException {
        throw getUnsupportedException("CREATE");
    }

    @Override
    public boolean delete(IntegrationSystemDto entity) throws AqualityException {
        throw getUnsupportedException("DELETE");
    }

    private AqualityException getUnsupportedException(String operationName) {
        return new AqualityException("Operation " + operationName + " is not supported for integration_systems table. This table includes list of constants.");
    }
}
