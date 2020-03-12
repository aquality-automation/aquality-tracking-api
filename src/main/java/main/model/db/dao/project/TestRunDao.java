package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.TestRunDto;

public class TestRunDao extends DAO<TestRunDto> {
    public TestRunDao() {
        super(TestRunDto.class);
        select = "{call SELECT_TEST_RUN(?,?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST_RUN(?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST_RUN(?)}";
    }
}
