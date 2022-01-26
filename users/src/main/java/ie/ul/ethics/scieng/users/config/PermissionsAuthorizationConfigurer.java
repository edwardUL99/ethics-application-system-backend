package ie.ul.ethics.scieng.users.config;

import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import ie.ul.ethics.scieng.users.authorization.PermissionsAuthorizer;
import ie.ul.ethics.scieng.users.authorization.RequestMethod;
import ie.ul.ethics.scieng.users.authorization.RequiredPermissions;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * This class configures the PermissionsAuthorizer object to authorize permissions
 */
@Log4j2
public class PermissionsAuthorizationConfigurer {
    /**
     * The map of required permissions
     */
    private final Map<PermissionsAuthorizer.Path, RequiredPermissions> requiredPermissions;

    /**
     * Create a permissions configurer object
     */
    public PermissionsAuthorizationConfigurer() {
        this.requiredPermissions = new HashMap<>();
    }

    /**
     * Get the permissions matching the names
     * @param permissionNames the names of the permissions as fields (eg. Permissions.CREATE_APPLICATION and not Create Application)
     * @return the collection of matching permissions
     */
    private Collection<Permission> getPermissions(String...permissionNames) {
        Collection<Permission> permissions = new LinkedHashSet<>();

        for (String name : permissionNames) {
            Permission permission = Permissions.getPermissionByFieldName(name);

            if (permission == null)
                throw new IllegalArgumentException("No suitable permission found for permission name " + name);

            permissions.add(permission);
        }

        return permissions;
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has at least one permission
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissionNames the names of the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireOneOfPermissions(String antPath, String...permissionNames) {
        return requirePermissions(antPath, RequestMethod.ALL, getPermissions(permissionNames), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has at least one permission
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param requestMethod the request method to use. Use ALL to specify all request methods should be protected
     * @param permissionNames the names of the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireOneOfPermissions(String antPath, RequestMethod requestMethod, String...permissionNames) {
        return requirePermissions(antPath, requestMethod, getPermissions(permissionNames), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has at least one permission
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireOneOfPermissions(String antPath, Permission...permissions) {
        return requirePermissions(antPath, RequestMethod.ALL, Arrays.asList(permissions), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has at least one permission
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param requestMethod the request method to use. Use ALL to specify all request methods should be protected
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireOneOfPermissions(String antPath, RequestMethod requestMethod, Permission...permissions) {
        return requirePermissions(antPath, requestMethod, Arrays.asList(permissions), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissionNames the names of the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireAllPermissions(String antPath, String...permissionNames) {
        return requirePermissions(antPath, RequestMethod.ALL, getPermissions(permissionNames), true);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param requestMethod the request method to use. Use ALL to specify all request methods should be protected
     * @param permissionNames the names of the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireAllPermissions(String antPath, RequestMethod requestMethod, String...permissionNames) {
        return requirePermissions(antPath, requestMethod, getPermissions(permissionNames), true);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireAllPermissions(String antPath, Permission...permissions) {
        return requirePermissions(antPath, RequestMethod.ALL, Arrays.asList(permissions), true);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param requestMethod the request method to use. Use ALL to specify all request methods should be protected
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requireAllPermissions(String antPath, RequestMethod requestMethod, Permission...permissions) {
        return requirePermissions(antPath, requestMethod, Arrays.asList(permissions),true);
    }

    /**
     * Configures required permissions for the provided ant path and permissions. If requireAll is true, the user's role
     * must all be contained in the provided permissions, otherwise only one is required
     * @param antPath the api path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param requestMethod the request method to use. Only requests with this request method specified are protected.
     *                      If the same ant path is registered with ALL and another Method, if the request method matches the other
     *                      method, it overrides ALL, else ALL is used
     * @param permissions the permissions required to access this api path
     * @param requireAll true to require all permissions to be satisfied by a user's role, else at least one
     * @return an instance of this for chaining
     */
    public PermissionsAuthorizationConfigurer requirePermissions(String antPath, RequestMethod requestMethod, Collection<Permission> permissions, boolean requireAll) {
        log.debug("Configuring path {} to require permissions {} for request method {}, and require all permissions: {}",
                antPath, requestMethod, permissions, requireAll);

        if (!antPath.endsWith("*") && !antPath.endsWith("/"))
            antPath += "/";

        RequiredPermissions requiredPermissions = new RequiredPermissions(permissions, requireAll);
        this.requiredPermissions.put(new PermissionsAuthorizer.Path(antPath, requestMethod), requiredPermissions);

        return this;
    }

    /**
     * Builds the authorizer based on the configuration
     * @return the PermissionsAuthorizer
     */
    public PermissionsAuthorizer getAuthorizer() {
        return new PermissionsAuthorizer(this.requiredPermissions);
    }
}
