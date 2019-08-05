package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestResultStatDto;

public class TestResultStatDao extends DAO<TestResultStatDto>{
    public TestResultStatDao() {
        super(TestResultStatDto.class);
        select = "{call SELECT_RESULTS_STAT(?,?,?)}";
    }
}
