package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.systems.SystemDto;

public class SystemDao extends DAO<SystemDto> {

    public SystemDao() {
        super(SystemDto.class);
        select = "{call SELECT_INT_SYSTEMS(?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_INT_SYSTEMS(?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_INT_SYSTEMS(?)}";
    }
}
