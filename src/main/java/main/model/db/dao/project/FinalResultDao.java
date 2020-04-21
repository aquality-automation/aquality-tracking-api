package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.FinalResultDto;

public class FinalResultDao extends DAO<FinalResultDto> {
    public FinalResultDao() {
        super(FinalResultDto.class);
        select = "{call SELECT_FINAL_RESULT(?,?)}";
    }
}
