package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.authorization.Permissions;
import ie.ul.edward.ethics.users.authorization.RequestMethod;
import static ie.ul.edward.ethics.common.Constants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This class configures the paths that require certain permissions to access
 */
@Configuration
public class PermissionsConfiguration {
    /**
     * The JSON file providing permissions paths configuration
     */
    private final PermissionsPathsConfig permissionsPathsConfig;

    /**
     * Create the permissions configuration with the provided PermissionsPathsConfig object
     * @param permissionsPathsConfig the config object containing the listing of paths
     */
    @Autowired
    public PermissionsConfiguration(PermissionsPathsConfig permissionsPathsConfig) {
        this.permissionsPathsConfig = permissionsPathsConfig;
    }

    /**
     * Configures the permissions required for paths
     * @param permissionsConfigurer the configuration object
     */
    @Autowired
    public void configurePermissions(PermissionsAuthorizationConfigurer permissionsConfigurer) {
        List<PermissionsPathsConfig.ConfiguredPath> configuredPaths = permissionsPathsConfig.getPaths();

        if (configuredPaths == null || configuredPaths.size() == 0) {
            // TODO put default paths here (may need to configure permissions endpoint for all methods. if so, change permissions.json too)
            String permissionsEndpoint = createApiPath(Endpoint.USERS, "permissions");
            permissionsConfigurer
                    .requireOneOfPermissions(permissionsEndpoint, RequestMethod.POST, Permissions.GRANT_PERMISSIONS)
                    .requireOneOfPermissions(permissionsEndpoint, RequestMethod.PUT, Permissions.GRANT_PERMISSIONS)
                    .requireAllPermissions("/api/**/admin/**", Permissions.ADMIN);
        } else {
            for (PermissionsPathsConfig.ConfiguredPath configuredPath : configuredPaths) {
                String path = configuredPath.getPath();
                String[] permissions = configuredPath.getPermissions();
                RequestMethod[] methods = configuredPath.getRequestMethods();
                boolean requireAll = configuredPath.isRequireAll();

                for (RequestMethod requestMethod : methods) {
                    if (requireAll) {
                        permissionsConfigurer
                                .requireAllPermissions(path, requestMethod, permissions);
                    } else {
                        permissionsConfigurer
                                .requireOneOfPermissions(path, requestMethod, permissions);
                    }
                }
            }
        }
    }
}
