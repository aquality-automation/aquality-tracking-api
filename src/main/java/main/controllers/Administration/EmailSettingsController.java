package main.controllers.Administration;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.EmailSettingsDto;
import main.model.dto.UserDto;

import java.util.List;

public class EmailSettingsController extends BaseController<EmailSettingsDto> {
    private EmailSettingsDao emailSettingsDao;

    public EmailSettingsController(UserDto user) {
        super(user);
        emailSettingsDao = new EmailSettingsDao();
    }

    @Override
    public List<EmailSettingsDto> get(EmailSettingsDto entity) throws RPException {
        if(baseUser.isAdmin()){
            return emailSettingsDao.getAll();
        }else{
            throw new RPPermissionsException("Account is not allowed to view Email Settings", baseUser);
        }
    }

    @Override
    public EmailSettingsDto create(EmailSettingsDto template) throws RPException {
        if(baseUser.isAdmin()){
            return emailSettingsDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to update Email Settings", baseUser);
        }
    }
    @Override
    public boolean delete(EmailSettingsDto entity) throws RPException {
        return false;
    }

    public boolean getEmailStatus() throws RPException {
        return emailSettingsDao.getAll().get(0).getEnabled() > 0;
    }
}
