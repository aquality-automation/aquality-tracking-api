package main.controllers.Integrations;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.integrations.IntegrationTestDao;
import main.model.dto.integrations.IntegrationTestDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class IntegrationTestController extends BaseController<IntegrationTestDto> {

    private final IntegrationTestDao integrationTestDao;

    public IntegrationTestController(UserDto user) {
        super(user);
        integrationTestDao = new IntegrationTestDao();
    }

    @Override
    public List<IntegrationTestDto> get(IntegrationTestDto entity) throws AqualityException {
        return null;
    }

    @Override
    public IntegrationTestDto create(IntegrationTestDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return integrationTestDao.create(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add link to the integration system", baseUser);
        }
    }

    @Override
    public boolean delete(IntegrationTestDto entity) throws AqualityException {
        return false;
    }
}
