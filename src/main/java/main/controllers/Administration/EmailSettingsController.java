package main.controllers.Administration;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.EmailSettingsDto;
import main.model.dto.UserDto;
import org.apache.poi.util.NotImplemented;

import java.util.List;

public class EmailSettingsController extends BaseController<EmailSettingsDto> {
    private EmailSettingsDao emailSettingsDao;

    public EmailSettingsController(UserDto user) {
        super(user);
        emailSettingsDao = new EmailSettingsDao();
    }

    @Override
    public List<EmailSettingsDto> get(EmailSettingsDto entity) throws AqualityException {
        permissionsChecker.checkAdmin("Account is not allowed to view Email Settings");
        return emailSettingsDao.getAll();
    }

    @Override
    public EmailSettingsDto create(EmailSettingsDto template) throws AqualityException {
        permissionsChecker.checkAdmin("Account is not allowed to update Email Settings");
        return emailSettingsDao.create(template);
    }

    @Override @NotImplemented
    public boolean delete(EmailSettingsDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    public boolean isEmailEnabled() throws AqualityException {
        return emailSettingsDao.getAll().get(0).getEnabled() > 0;
    }
}
