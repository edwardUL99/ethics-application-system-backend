package ie.ul.edward.ethics.common.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides configuration properties for email
 */
@Configuration
@ConfigurationProperties(prefix="email")
@Data
public class EmailConfigurationProperties {
    /**
     * The sender email
     */
    private String from;
    /**
     * The host of the email server
     */
    private String host;
    /**
     * The port of the email server
     */
    private String port;
    /**
     * The password of the email account
     */
    private String password;
    /**
     * Enables/Disables email debug
     */
    private boolean debug;
}
