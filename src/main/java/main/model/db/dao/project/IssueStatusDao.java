package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.audit.AuditStatusDto;
import main.model.dto.project.IssueStatusDto;

public class IssueStatusDao extends DAO<IssueStatusDto> {
    public IssueStatusDao() {
        super(IssueStatusDto.class);
        select = "{call SELECT_ISSUE_STATUS(?)}";
    }
}
