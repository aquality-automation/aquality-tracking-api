package main.controllers.integrations;

import main.controllers.ProjectEntityController;
import main.model.db.dao.integrations.SystemDao;
import main.model.dto.integrations.SystemDto;
import main.model.dto.settings.UserDto;

public class SystemController extends ProjectEntityController<SystemDto, SystemDao> {

    public SystemController(UserDto user) {
        super(user, new SystemDao());
    }
}
