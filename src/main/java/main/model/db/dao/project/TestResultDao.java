package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestResultDto;

public class TestResultDao extends DAO<TestResultDto> {
    public TestResultDao() {
        super(TestResultDto.class);
        select = "{call SELECT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST_RESULT(?)}";
    }
}
