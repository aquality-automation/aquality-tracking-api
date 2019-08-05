package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.TestRunLabelDto;

public class TestRunLabelDao extends DAO<TestRunLabelDto> {
    public TestRunLabelDao() {
        super(TestRunLabelDto.class);
        select = "{call SELECT_TEST_RUN_LABELS(?)}";
    }
}
