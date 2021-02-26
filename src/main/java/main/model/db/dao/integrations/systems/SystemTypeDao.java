package main.model.db.dao.integrations.systems;

import main.model.db.dao.DAO;
import main.model.dto.integrations.systems.SystemTypeDto;

public class SystemTypeDao extends DAO<SystemTypeDto> {

    public SystemTypeDao(){
        super(SystemTypeDto.class);
        select = "{call SELECT_INT_SYSTEM_TYPE()}";
    }
}
