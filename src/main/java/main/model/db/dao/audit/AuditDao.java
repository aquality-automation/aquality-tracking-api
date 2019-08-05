package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.AuditDto;


public class AuditDao extends DAO<AuditDto> {

    public AuditDao() {
        super(AuditDto.class);
        select = "{call SELECT_AUDIT(?,?,?)}";
        insert = "{call INSERT_AUDIT(?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_AUDIT(?)}";
    }
}
