package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestRunStatisticDto;

public class TestRunStatisticDao extends DAO<TestRunStatisticDto> {
    public TestRunStatisticDao() {
        super(TestRunStatisticDto.class);
        select = "{call SELECT_TEST_RUN_STATS(?,?,?,?)}";
    }
}
