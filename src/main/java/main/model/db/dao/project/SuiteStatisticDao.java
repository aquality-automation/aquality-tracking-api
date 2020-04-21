package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.SuiteStatisticDto;

public class SuiteStatisticDao extends DAO<SuiteStatisticDto> {
    public SuiteStatisticDao() {
        super(SuiteStatisticDto.class);
        select = "{call SELECT_TEST_STATS(?)}";
    }
}
