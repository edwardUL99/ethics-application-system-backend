package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.models.roles.Permission;
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
    public void configurePermissions(PermissionsConfigurer permissionsConfigurer) {
        permissionsConfigurer
                .requireOneOfPermissions("/api/auth/account", Permission.CREATE_APPLICATION); // todo put different paths here
    }
}
