package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.AuditStatisticDto;

public class AuditStatisticDao extends DAO<AuditStatisticDto> {
    public AuditStatisticDao() {
        super(AuditStatisticDto.class);
        select = "{call SELECT_AUDIT_STATS()}";
    }
}
