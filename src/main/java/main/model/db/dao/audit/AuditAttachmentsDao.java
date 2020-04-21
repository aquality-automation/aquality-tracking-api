package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.audit.AuditAttachmentDto;

public class AuditAttachmentsDao extends DAO<AuditAttachmentDto>{
    public AuditAttachmentsDao() {
        super(AuditAttachmentDto.class);
        select = "{call SELECT_AUDIT_ATTACH(?,?)}";
        insert = "{call INSERT_AUDIT_ATTACH(?,?)}";
        remove = "{call REMOVE_AUDIT_ATTACH(?)}";
    }
}
