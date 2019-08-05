package main.utils;

import main.model.dto.EmailSettingsDto;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

public class EmailUtil {

    private EmailSettingsDto emailSettingsDto;

    public EmailUtil(EmailSettingsDto emailSettingsDto){
        this.emailSettingsDto = emailSettingsDto;
    }

    public void SendHtmlEmail(List<String> to, String subject, String content) throws MessagingException, IOException, URISyntaxException {
        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", emailSettingsDto.getHost());
        properties.put("mail.smtp.port", emailSettingsDto.getPort());
        Session session;
        if(emailSettingsDto.getUse_auth() != 1){
            properties.setProperty("mail.smtp.auth", "false");
            properties.setProperty("mail.user", emailSettingsDto.getUser());
            properties.setProperty("mail.password", emailSettingsDto.getPassword());

            session = Session.getDefaultInstance(properties);
        } else {
            properties.setProperty("mail.smtp.auth", "true");
            SMTPAuthenticator smtpAuthenticator = new SMTPAuthenticator();

            session = Session.getInstance(properties, smtpAuthenticator);
        }

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailSettingsDto.getFrom_email()));
            for(String email: to){
                if(!emailSettingsDto.getFrom_email().endsWith("@a1qa.com") || email.endsWith("@a1qa.com") || email.endsWith("@itransition.com")){
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                }
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
            imageBodyPart.attachFile(new File(classloader.getResource("rp-logo.png").toURI()));

            multipart.addBodyPart(imageBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        }catch(SendFailedException e){
            for(String email: to){
                System.out.println("Can't send email to: " + email);
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = emailSettingsDto.getUser();
            String password = emailSettingsDto.getPassword();
            return new PasswordAuthentication(username, password);
        }
    }
}
