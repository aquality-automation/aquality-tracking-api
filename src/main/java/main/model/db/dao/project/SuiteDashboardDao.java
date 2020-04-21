package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.SuiteDashboardDto;

public class SuiteDashboardDao extends DAO<SuiteDashboardDto>{
    public SuiteDashboardDao() {
        super(SuiteDashboardDto.class);
        select = "{call SELECT_SUITE_DASHBOARD(?,?)}";
        insert = "{call INSERT_SUITE_DASHBOARD(?,?,?,?)}";
        remove = "{call REMOVE_SUITE_DASHBOARD(?)}";
    }
}
