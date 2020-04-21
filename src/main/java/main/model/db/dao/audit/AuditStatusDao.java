package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.audit.AuditStatusDto;

public class AuditStatusDao extends DAO<AuditStatusDto> {
    public AuditStatusDao() {
        super(AuditStatusDto.class);
        select = "{call SELECT_AUDIT_STATUS(?)}";
    }
}
