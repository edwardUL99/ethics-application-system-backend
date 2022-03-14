package ie.ul.ethics.scieng.users.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents the configuration properties for user permissions authorization
 */
@Configuration
@ConfigurationProperties(prefix = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserPermissionsConfig {
    /**
     * This property determines if user permission authorization should be enabled or disabled
     */
    private boolean enabled;
    /**
     * The email of the chair. When a user is registered with the same email, they will be assigned the chair
     * person role
     */
    private String chair;

    /**
     * The system property set if and only if isEnabled returns false
     */
    public static final String USER_PERMISSIONS_DISABLED = "USER.PERMISSIONS.DISABLED";

    /**
     * Checks if permissions have been configured to be disabled
     * @return true if disabled, false if enabled
     */
    public static boolean permissionsDisabled() {
        return System.getProperty(USER_PERMISSIONS_DISABLED, "false").equalsIgnoreCase("true");
    }

    /**
     * Returns true if permissions authorization is enabled, false otherwise
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get the email of the chair person.
     * @return the chair person email
     */
    public String getChair() {
        String chairEnv = System.getenv("ETHICS_CHAIR_EMAIL");
        chair = (chairEnv != null) ? chairEnv:chair;

        if (chair == null)
            throw new IllegalStateException("You need to define the permissions.chair property with the email of the chair person");

        return chair;
    }
}
