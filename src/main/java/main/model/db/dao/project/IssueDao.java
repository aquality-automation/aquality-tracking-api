package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.IssueDto;

public class IssueDao extends DAO<IssueDto> {

    public IssueDao() {
        super(IssueDto.class);
        select = "{call SELECT_ISSUES(?,?,?,?,?,?,?)}";
        insert = "{call INSERT_ISSUE(?,?,?,?,?,?,?,?,?,?,?)}";
    }
}
