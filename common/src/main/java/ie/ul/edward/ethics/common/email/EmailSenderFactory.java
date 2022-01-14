package ie.ul.edward.ethics.common.email;

import ie.ul.edward.ethics.common.email.config.EmailConfigurationProperties;
import lombok.extern.log4j.Log4j2;

/**
 * This class provides a factory for retrieving the instance of EmailSender to use
 */
@Log4j2
public final class EmailSenderFactory {
    /**
     * Retrieve the email sender implementation
     * @param configurationProperties the configuration properties to configure email
     * @return the implementation of email sender to use in the system
     */
    public static EmailSender getEmailSender(EmailConfigurationProperties configurationProperties) {
        EmailSender sender;
        if (System.getProperty("email.disable") != null) {
            log.info("System property email.disable is set, so sendEmail will be a no-op");
            sender = new NoopEmailSender();
        } else {
            sender = new DefaultEmailSender(configurationProperties);
        }

        log.info("Emails will be sent using the EmailSender implementation class {}", sender);

        return sender;
    }
}
