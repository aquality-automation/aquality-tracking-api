package main.controllers.integrations;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.ReferenceDto;
import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.settings.UserDto;

import java.util.List;

public class ReferenceController<DTO extends ReferenceDto> extends BaseController<DTO> {

    private final ReferenceDao<DTO> referenceDao;

    public ReferenceController(UserDto user, ReferenceType<DTO> referenceType) {
        super(user);
        referenceDao = new ReferenceDao<DTO>(referenceType);
    }

    @Override
    public List<DTO> get(DTO entity) throws AqualityException {
        return null;
    }

    @Override
    public DTO create(DTO entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return referenceDao.create(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to add link to the integration system", baseUser);
        }
    }

    @Override
    public boolean delete(DTO entity) throws AqualityException {
        return false;
    }
}
