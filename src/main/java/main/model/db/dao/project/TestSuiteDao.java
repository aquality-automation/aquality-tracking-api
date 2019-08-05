package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestSuiteDto;

public class TestSuiteDao extends DAO<TestSuiteDto>{
    public TestSuiteDao() {
        super(TestSuiteDto.class);
        select = "{call SELECT_SUITE(?,?,?)}";
        insert = "{call INSERT_SUITE(?,?,?)}";
        remove = "{call REMOVE_SUITE(?)}";
    }
}
