package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.types.SystemTypeDto;

public class SystemTypeDao extends DAO<SystemTypeDto> {

    public SystemTypeDao(){
        super(SystemTypeDto.class);
        select = "{call SELECT_INT_SYSTEM_TYPE()}";
    }
}
