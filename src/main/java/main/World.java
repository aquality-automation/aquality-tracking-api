package main;

import lombok.Getter;
import main.exceptions.AqualityException;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.settings.EmailSettingsDto;

public class World {

    private static World instance;

    @Getter
    private String baseURL;


    private World() {
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        EmailSettingsDto settings;
        try {
            settings = emailSettingsDao.getEntityById(1);
            baseURL = settings.getBase_url();
        } catch (AqualityException e) {
            e.printStackTrace();
            System.out.println("World cannot be initialized!");
        }
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public static World updateInstance() {
        instance = new World();
        return instance;
    }
}
