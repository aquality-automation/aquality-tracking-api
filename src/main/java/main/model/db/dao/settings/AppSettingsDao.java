package main.model.db.dao.settings;

import main.model.db.dao.DAO;
import main.model.dto.AppSettingsDto;

public class AppSettingsDao extends DAO<AppSettingsDto> {

    public AppSettingsDao(){
        super(AppSettingsDto.class);
        insert = "{call INSERT_APP_SETTINGS(?,?,?,?)}";
        select = "{call SELECT_APP_SETTINGS()}";
    }
}
