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

    /**
     * Sets the sender email address
     */
    public static final String ENV_FROM = "ETHICS_EMAIL_FROM";
    /**
     * The host of the mail server
     */
    public static final String ENV_HOST = "ETHICS_EMAIL_HOST";
    /**
     * The port of the email server
     */
    public static final String ENV_PORT = "ETHICS_EMAIL_PORT";
    /**
     * The password of the email server
     */
    public static final String ENV_PASSWORD = "ETHICS_EMAIL_PASSWORD";
    /**
     * A variable for enabling email debug
     */
    public static final String ENV_DEBUG = "ETHICS_EMAIL_DEBUG";

    /**
     * If there are environment variables configuring email properties, use those properties to override the ones given
     * in the properties file
     * @param properties the properties to merge the environment into
     */
    public static void mergeFromEnvironment(EmailConfigurationProperties properties) {
        String from = System.getenv(ENV_FROM);
        String host = System.getenv(ENV_HOST);
        String port = System.getenv(ENV_PORT);
        String password = System.getenv(ENV_PASSWORD);
        String debug = System.getenv(ENV_DEBUG);

        if (from != null)
            properties.from = from;

        if (host != null)
            properties.host = host;

        if (port != null)
            properties.port = port;

        if (password != null)
            properties.password = password;

        if (debug != null)
            properties.debug = true;
    }
}
