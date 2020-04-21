package main.utils;

import main.exceptions.AqualityException;
import main.model.email.AuditEmails;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.EmailDto;
import main.model.dto.settings.EmailSettingsDto;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

public class Daily implements Job {

    @Override
    public void execute(final JobExecutionContext ctx) {
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        try {
            EmailSettingsDto settings = emailSettingsDao.getEntityById(1);
            settings.setPassword(emailSettingsDao.getAdminSecret(settings.getPassword()));
            AuditEmails emailWorker = new AuditEmails();
            EmailUtil emailUtil = new EmailUtil(settings);
            try {
                List<EmailDto> emails = emailWorker.getUpcomingEmails();
                List<EmailDto> OverdueAuditsEmails = emailWorker.getOverdueEmails();
                emails.addAll(OverdueAuditsEmails);
                for(EmailDto email: emails){
                    emailUtil.sendHtmlEmail(email.getRecipients(), email.getSubject(), email.getContent());
                }
                System.out.println("Daily Audits Notifications were sent.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (AqualityException e) {
            e.printStackTrace();
        }
    }
}