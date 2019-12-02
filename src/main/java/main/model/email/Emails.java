package main.model.email;

import main.exceptions.AqualityException;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.EmailDto;
import main.model.dto.EmailSettingsDto;
import main.utils.DateUtils;
import main.utils.EmailUtil;

abstract class Emails {
    String baseURL;
    DateUtils dateUtils = new DateUtils();

    Emails() throws AqualityException {
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        EmailSettingsDto settings = emailSettingsDao.getEntityById(new EmailSettingsDto());
        baseURL = settings.getBase_url();
    }

    boolean sendEmail(EmailDto email) throws AqualityException {
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        EmailSettingsDto settings = emailSettingsDao.getAll().get(0);
        settings.setPassword(emailSettingsDao.getAdminSecret(settings.getPassword()));
        EmailUtil emailUtil = new EmailUtil(settings);
        return emailUtil.sendHtmlEmail(email.getRecipients(), email.getSubject(), email.getContent());
    }
}
