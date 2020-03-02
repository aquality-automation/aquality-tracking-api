package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.MilestoneDto;

public class MilestoneDao extends DAO<MilestoneDto> {

    public MilestoneDao(){
        super(MilestoneDto.class);
        select = "{call SELECT_MILESTONE(?,?,?,?)}";
        insert = "{call INSERT_MILESTONE(?,?,?,?,?)}";
        remove = "{call REMOVE_MILESTONE(?)}";
    }
}
