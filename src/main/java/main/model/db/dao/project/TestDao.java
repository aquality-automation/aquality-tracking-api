package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestDto;

public class TestDao extends DAO<TestDto> {
    public TestDao() {
        super(TestDto.class);
        select = "{call SELECT_TEST(?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST(?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST(?)}";
    }
}
