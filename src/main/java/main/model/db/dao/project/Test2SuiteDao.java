package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.Test2SuiteDto;

public class Test2SuiteDao extends DAO<Test2SuiteDto> {
    public Test2SuiteDao() {
        super(Test2SuiteDto.class);
        select = "{call SELECT_TEST_SUITES(?,?)}";
        insert = "{call INSERT_TEST_TO_SUITE(?,?)}";
        remove = "{call REMOVE_TEST_FROM_SUITE(?,?)}";
    }
}
