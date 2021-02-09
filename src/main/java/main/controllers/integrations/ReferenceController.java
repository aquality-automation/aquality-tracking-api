package main.controllers.integrations;

import main.controllers.ProjectEntityController;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.ReferenceDto;
import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.roles.GlobalRole;
import main.model.dto.roles.ProjectRole;
import main.model.dto.roles.RolePair;
import main.model.dto.settings.UserDto;

public class ReferenceController<T extends ReferenceDto> extends ProjectEntityController<T, ReferenceDao<T>> {

    public ReferenceController(UserDto user, ReferenceType<T> referenceType) {
        super(user, new ReferenceDao<>(referenceType));
    }

    //TODO: make this working
    @Override
    protected RolePair getRolesPermittedToDelete() {
        return new RolePair(GlobalRole.ADMIN, ProjectRole.ENGINEER);
    }
}
