package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.PredefinedResolutionDto;

public class PredefinedResolutionDao extends DAO<PredefinedResolutionDto> {
    public PredefinedResolutionDao() {
        super(PredefinedResolutionDto.class);
        select = "{call SELECT_PREDEFINED_RESOLUTION(?,?)}";
        insert = "{call INSERT_PREDEFINED_RESOLUTION(?,?,?,?,?,?)}";
        remove = "{call REMOVE_PREDEFINED_RESOLUTION(?)}";
    }
}
