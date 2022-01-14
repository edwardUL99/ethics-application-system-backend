package ie.ul.edward.ethics.common.email;

import ie.ul.edward.ethics.common.email.exceptions.EmailException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * This provides the default implementation of the EmailSender interface
 */
@Component
@Log4j2
public class DefaultEmailSender implements EmailSender {
    /**
     * Configuration properties for the email package
     */
    private final EmailConfigurationProperties configurationProperties;
    /**
     * The java mail session
     */
    private final Session session;

    /**
     * Create a DefaultEmailSender with the provided properties
     * @param configurationProperties the properties for configuring email
     */
    @Autowired
    public DefaultEmailSender(EmailConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;

        if (System.getProperty("email.disable") != null) {
            this.session = null;
            log.info("System property email.disable is set, so sendEmail will be a no-op");
        } else {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", configurationProperties.getHost());
            properties.put("mail.smtp.port", configurationProperties.getPort());
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(configurationProperties.getFrom(), configurationProperties.getPassword());
                }
            });

            session.setDebug(configurationProperties.isDebug());

            this.session = session;
        }
    }

    /**
     * Send the email to the specified recipient
     *
     * @param to    the email address of the recipient
     * @param subject the subject to add to the email
     * @param email the email to send
     * @throws EmailException if an error occurs sending the email
     */
    @Override
    public void sendEmail(String to, String subject, String email) throws EmailException {
        if (this.session == null)
            return;

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(configurationProperties.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(email, "text/html");
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException ex) {
            throw new EmailException("An error occurred sending email", ex);
        }
    }
}
