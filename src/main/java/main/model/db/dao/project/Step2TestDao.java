package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.Step2TestDto;

public class Step2TestDao extends DAO<Step2TestDto> {
    public Step2TestDao() {
        super(Step2TestDto.class);
        insert = "{call INSERT_STEP_TO_TEST(?,?)}";
        remove = "{call REMOVE_STEP_TO_TEST(?)}";
    }
}
