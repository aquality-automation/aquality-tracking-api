package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.ResultResolutionDto;

public class ResultResolutionDao extends DAO<ResultResolutionDto> {
    public ResultResolutionDao() {
        super(ResultResolutionDto.class);
        select = "{call SELECT_RESULT_RESOLUTION(?,?)}";
        insert = "{call INSERT_RESULT_RESOLUTION(?,?,?,?)}";
        remove = "{call REMOVE_RESULT_RESOLUTION(?)}";
    }
}
