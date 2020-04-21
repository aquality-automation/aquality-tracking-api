package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.Suite2DashboardDto;

public class Suite2DashboardDao extends DAO<Suite2DashboardDto>{
    public Suite2DashboardDao() {
        super(Suite2DashboardDto.class);
        select = "{call SELECT_SUITES_FROM_DASHBOARD(?)}";
        insert = "{call INSERT_SUITE_TO_DASHBOARD(?,?)}";
        remove = "{call REMOVE_SUITE_FROM_DASHBOARD(?)}";
    }
}
