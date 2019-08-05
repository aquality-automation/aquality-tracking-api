package main.utils;

import main.model.email.AuditEmails;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.EmailDto;
import main.model.dto.EmailSettingsDto;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class Daily implements Job {

    @Override
    public void execute(final JobExecutionContext ctx) throws JobExecutionException {
        AuditEmails emailWorker = new AuditEmails();
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        EmailUtil emailUtil = null;
        try {
            EmailSettingsDto settings = emailSettingsDao.getAll().get(0);
            settings.setPassword(emailSettingsDao.getAdminSecret(settings.getPassword()));
            emailUtil = new EmailUtil(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<EmailDto> emails = emailWorker.GetUpcomingEmails();
            List<EmailDto> OverdueAuditsEmails = emailWorker.GetOverdueEmails();
            emails.addAll(OverdueAuditsEmails);
            for(EmailDto email: emails){
                emailUtil.SendHtmlEmail(email.getRecipients(), email.getSubject(), email.getContent());
            }
            System.out.println("Daily Audits Notifications were sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}