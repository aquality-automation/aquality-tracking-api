package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.UserSessionDto;

public class UserSessionDao extends DAO<UserSessionDto> {
    public UserSessionDao() {
        super(UserSessionDto.class);
        insert = "{call INSERT_SESSION(?,?)}";
    }
}
