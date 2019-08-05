package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.SuiteDashboardDto;
import main.model.dto.TestSuiteDto;

public class SuiteDashboardDao extends DAO<SuiteDashboardDto>{
    public SuiteDashboardDao() {
        super(SuiteDashboardDto.class);
        select = "{call SELECT_SUITE_DASHBOARD(?,?)}";
        insert = "{call INSERT_SUITE_DASHBOARD(?,?,?,?)}";
        remove = "{call REMOVE_SUITE_DASHBOARD(?)}";
    }
}
