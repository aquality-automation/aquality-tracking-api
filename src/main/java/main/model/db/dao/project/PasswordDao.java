package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.settings.PasswordDto;

public class PasswordDao extends DAO<PasswordDto> {
    public PasswordDao() {
        super(PasswordDto.class);
        insert = "{call INSERT_PASSWORD(?,?,?)}";
    }
}
