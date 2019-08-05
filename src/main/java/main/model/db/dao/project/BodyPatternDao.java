package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.BodyPatternDto;

public class BodyPatternDao extends DAO<BodyPatternDto> {

    public BodyPatternDao(){
        super(BodyPatternDto.class);
        select = "{call SELECT_BODY_PATTERN(?,?,?)}";
        insert = "{call INSERT_BODY_PATTERN(?,?,?)}";
        remove = "{call REMOVE_BODY_PATTERN(?)}";
    }
}
