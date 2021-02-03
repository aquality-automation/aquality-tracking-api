package main.controllers.integrations;

import main.controllers.ProjectEntityController;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.ReferenceDto;
import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.settings.UserDto;

public class ReferenceController<DTO extends ReferenceDto> extends ProjectEntityController<DTO, ReferenceDao<DTO>> {

    public ReferenceController(UserDto user, ReferenceType<DTO> referenceType) {
        super(user, new ReferenceDao<>(referenceType));
    }
}
