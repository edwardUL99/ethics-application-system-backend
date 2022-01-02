package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.authorization.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures the paths that require certain permissions to access
 */
@Configuration
public class PermissionsConfiguration {
    /**
     * Configures the permissions required for paths
     * @param permissionsConfigurer the configuration object
     */
    @Autowired
    public void configurePermissions(PermissionsAuthorizationConfigurer permissionsConfigurer) {
        permissionsConfigurer
                .requireOneOfPermissions("/api/auth/account", Permissions.CREATE_APPLICATION); // todo put different paths here
    }
}
