package ie.ul.ethics.scieng.common.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * This class provides configuration properties for email
 */
@Configuration
@ConfigurationProperties(prefix="email")
@Order(1)
@Data
public class EmailConfigurationProperties {
    /**
     * The sender email
     */
    private String from = "";
    /**
     * The host of the email server
     */
    private String host = "";
    /**
     * The port of the email server
     */
    private String port = "";
    /**
     * The password of the email account
     */
    private String password = "";
    /**
     * Enables/Disables email debug
     */
    private boolean debug;
}
