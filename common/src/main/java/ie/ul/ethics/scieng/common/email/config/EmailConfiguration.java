package ie.ul.ethics.scieng.common.email.config;

import ie.ul.ethics.scieng.common.email.EmailSender;
import ie.ul.ethics.scieng.common.email.EmailSenderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * This class provides configuration for the email package
 */
@Configuration
@Order(2)
public class EmailConfiguration {
    /**
     * The configuration properties for email
     */
    private final EmailConfigurationProperties configurationProperties;

    /**
     * Construct an EmailConfiguration instance
     * @param configurationProperties the properties configuring the email package
     */
    public EmailConfiguration(EmailConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    /**
     * This method registers the bean for the EmailSender interface
     * @return the bean for the EmailSender interface
     */
    @Bean
    public EmailSender emailSender() {
        return EmailSenderFactory.getEmailSender(configurationProperties);
    }
}
