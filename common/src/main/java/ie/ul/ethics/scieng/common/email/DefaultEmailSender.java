package ie.ul.ethics.scieng.common.email;

import ie.ul.ethics.scieng.common.email.config.EmailConfigurationProperties;
import ie.ul.ethics.scieng.common.email.exceptions.EmailException;
import lombok.extern.log4j.Log4j2;

import javax.mail.*;
import java.io.File;
import java.util.Properties;

/**
 * This provides the default implementation of the EmailSender interface
 */
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
    public DefaultEmailSender(EmailConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
        EmailConfigurationProperties.mergeFromEnvironment(this.configurationProperties);

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

    /**
     * Send the email to the specified recipient
     *
     * @param to    the email address of the recipient
     * @param subject the subject to add to the email
     * @param email the email to send
     * @param attachments a list of attachments to attach to the email
     * @throws EmailException if an error occurs sending the email
     */
    @Override
    public void sendEmail(String to, String subject, String email, File...attachments) throws EmailException {
        AdvancedEmail advancedEmail = createAdvancedEmail()
                .setSubject(subject)
                .setFrom(configurationProperties.getFrom())
                .setTo(to)
                .setContent(email, true, true);

        for (File attachment : attachments)
            advancedEmail = advancedEmail.attachFile(attachment);

        sendEmail(advancedEmail);
    }

    /**
     * Send the advanced email by building it and sending it
     *
     * @param advancedEmail the email to build and send
     * @throws EmailException if an error occurs sending the email
     */
    @Override
    public void sendEmail(AdvancedEmail advancedEmail) throws EmailException {
        Message message = advancedEmail.buildMessage();

        try {
            Transport.send(message);
        } catch (MessagingException ex) {
            throw new EmailException("An error occurred sending email", ex);
        }
    }

    /**
     * Instantiate an instance of AdvancedEmail by passing in an instance of Session
     *
     * @return the advanced email instance
     */
    @Override
    public AdvancedEmail createAdvancedEmail() {
        return new AdvancedEmail(session);
    }
}
