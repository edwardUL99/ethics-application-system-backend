package ie.ul.ethics.scieng.users.config;

import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.authorization.RequestMethod;
import static ie.ul.ethics.scieng.common.Constants.*;
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
            String permissionsEndpoint = createApiPath(Endpoint.USERS, "user", "role");
            permissionsConfigurer
                    .requireOneOfPermissions(permissionsEndpoint, Permissions.GRANT_PERMISSIONS)
                    .requireAllPermissions("/api/**/admin/**", Permissions.ADMIN)
                    .requireOneOfPermissions(createApiPath(Endpoint.APPLICATIONS, "draft"), Permissions.CREATE_APPLICATION,
                            Permissions.EDIT_APPLICATION)
                    .requireOneOfPermissions(createApiPath(Endpoint.APPLICATIONS, true), RequestMethod.GET, Permissions.VIEW_OWN_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "id"), RequestMethod.GET, Permissions.CREATE_APPLICATION)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "review"), Permissions.REVIEW_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "review", "finish"), Permissions.REVIEW_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "assign"), Permissions.ASSIGN_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, true, "unassign", "**"), Permissions.ADMIN)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "approve"), Permissions.APPROVE_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "refer"), Permissions.REFER_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.APPLICATIONS, "resubmit"), Permissions.ASSIGN_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.EXPORT, true, "single"), Permissions.EXPORT_APPLICATIONS)
                    .requireAllPermissions(createApiPath(Endpoint.EXPORT, true, "range"), Permissions.EXPORT_APPLICATIONS);
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
