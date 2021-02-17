package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.publishing.PubItemDto;

public class PubItemDao extends DAO<PubItemDto> {

    public PubItemDao() {
        super(PubItemDto.class);
        select = "{call SELECT_INT_SYSTEM_LOG_PUBLISH(?,?,?,?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_INT_SYSTEM_LOG_PUBLISH(?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_INT_SYSTEM_LOG_PUBLISH(?)}";
    }
}
