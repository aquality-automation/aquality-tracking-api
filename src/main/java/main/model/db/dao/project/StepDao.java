package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.StepDto;

public class StepDao extends DAO<StepDto> {
    public StepDao() {
        super(StepDto.class);
        select = "{call SELECT_STEP(?,?,?)}";
        insert = "{call INSERT_STEP(?,?,?,?)}";
        remove = "{call REMOVE_STEP(?,?)}";
    }
}
