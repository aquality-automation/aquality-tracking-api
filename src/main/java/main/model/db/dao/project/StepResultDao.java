package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.StepResultDto;

public class StepResultDao extends DAO<StepResultDto> {
    public StepResultDao() {
        super(StepResultDto.class);
        select = "{call SELECT_STEP_RESULT(?,?,?)}";
        insert = "{call INSERT_STEP_RESULT(?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_STEP_RESULT(?)}";
    }
}
