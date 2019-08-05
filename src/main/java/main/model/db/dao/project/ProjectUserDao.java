package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.ProjectUserDto;

public class ProjectUserDao extends DAO<ProjectUserDto> {
    public ProjectUserDao() {
        super(ProjectUserDto.class);
        select = "{call SELECT_PROJECT_USERS(?,?)}";
        insert = "{call INSERT_PROJECT_USER(?,?,?,?,?)}";
        remove = "{call REMOVE_PROJECT_USER(?,?)}";
    }
}
