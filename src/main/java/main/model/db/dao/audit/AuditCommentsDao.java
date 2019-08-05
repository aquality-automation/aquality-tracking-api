package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.AuditCommentDto;

public class AuditCommentsDao extends DAO<AuditCommentDto> {
    public AuditCommentsDao() {
        super(AuditCommentDto.class);
        select = "{call SELECT_AUDIT_COMMENT(?,?)}";
        insert = "{call INSERT_AUDIT_COMMENT(?,?,?,?)}";
    }
}
