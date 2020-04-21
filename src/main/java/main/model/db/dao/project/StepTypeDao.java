package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.StepTypeDto;

public class StepTypeDao extends DAO<StepTypeDto> {
    public StepTypeDao() {
        super(StepTypeDto.class);
        select = "{call SELECT_STEP_TYPE()}";
    }
}
