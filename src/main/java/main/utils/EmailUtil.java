package main.utils;

import main.model.dto.settings.EmailSettingsDto;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class EmailUtil {

    private EmailSettingsDto emailSettingsDto;

    public EmailUtil(EmailSettingsDto emailSettingsDto) {
        this.emailSettingsDto = emailSettingsDto;
    }

    public boolean sendHtmlEmail(List<String> to, String subject, String content) {
        Session session = createSession();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailSettingsDto.getFrom_email()));
            for (String email : to) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent(content, "text/html; charset=utf-8");

            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart imageBodyPart = new MimeBodyPart();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            imageBodyPart.setHeader("Content-ID", "<logo>");
            imageBodyPart.setDisposition(Part.INLINE);
            imageBodyPart.attachFile(new File(classloader.getResource("logo.png").toURI()));

            multipart.addBodyPart(imageBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (SendFailedException e) {
            System.out.println("Can't send email to: " + String.join(",", to));
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Session createSession() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", emailSettingsDto.getHost());
        properties.put("mail.smtp.port", emailSettingsDto.getPort());
        properties.put("mail.smtp.starttls.enable", String.valueOf(emailSettingsDto.isStartTlsEnabled()));
        properties.put("mail.smtp.auth", String.valueOf(emailSettingsDto.isSmtpAuthEnabled()));

        Session session;
        if (!emailSettingsDto.isSmtpAuthEnabled()) {
            session = Session.getDefaultInstance(properties);
        } else {
            SMTPAuthenticator smtpAuthenticator = new SMTPAuthenticator();
            session = Session.getInstance(properties, smtpAuthenticator);
        }
        return session;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            String username = emailSettingsDto.getUser();
            String password = emailSettingsDto.getPassword();
            return new PasswordAuthentication(username, password);
        }
    }
}
