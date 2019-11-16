package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.ProjectDto;

public class ProjectDao extends DAO<ProjectDto> {
    public ProjectDao(){
        super(ProjectDto.class);
        select = "{call SELECT_PROJECT(?,?,?,?)}";
        insert = "{call INSERT_PROJECT(?,?,?,?,?)}";
        remove = "{call REMOVE_PROJECT(?)}";
    }
}
