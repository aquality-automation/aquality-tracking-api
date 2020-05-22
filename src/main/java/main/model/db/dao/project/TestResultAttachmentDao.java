package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.TestResultAttachmentDto;

public class TestResultAttachmentDao extends DAO<TestResultAttachmentDto> {
    public TestResultAttachmentDao() {
        super(TestResultAttachmentDto.class);
        select = "{call SELECT_TEST_RESULT_ATTACH(?,?)}";
        insert = "{call INSERT_TEST_RESULT_ATTACH(?,?,?)}";
        remove = "{call REMOVE_TEST_RESULT_ATTACH(?)}";
    }
}
