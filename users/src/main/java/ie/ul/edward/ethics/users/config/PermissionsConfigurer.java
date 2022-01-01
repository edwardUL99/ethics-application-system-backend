package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.models.roles.Permission;
import ie.ul.edward.ethics.users.repositories.PermissionRepository;
import ie.ul.edward.ethics.users.roles.UserAuthorizer;

import java.util.*;

/**
 * This class configures the UserAuthorizer object to authorize permissions
 */
public class PermissionsConfigurer {
    /**
     * The permission repository for reading and loading permissions
     */
    private final PermissionRepository permissionRepository;

    /**
     * The map of required permissions
     */
    private final Map<String, UserAuthorizer.RequiredPermissions> requiredPermissions;

    /**
     * Create a permissions configurer object
     * @param permissionRepository the repository required for matching permissions
     */
    public PermissionsConfigurer(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.requiredPermissions = new HashMap<>();
    }

    /**
     * Get the permissions matching the names
     * @param permissionNames the names of the permissions
     * @return the collection of matching permissions
     */
    private Collection<Permission> getPermissions(String...permissionNames) {
        Collection<Permission> permissions = new LinkedHashSet<>();

        for (String name : permissionNames) {
            Permission permission = permissionRepository.findByName(name).orElse(null);

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
    public PermissionsConfigurer requireOneOfPermissions(String antPath, String...permissionNames) {
        return requirePermissions(antPath, getPermissions(permissionNames), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has at least one permission
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsConfigurer requireOneOfPermissions(String antPath, Permission...permissions) {
        return requirePermissions(antPath, Arrays.asList(permissions), false);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissionNames the names of the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsConfigurer requireAllPermissions(String antPath, String...permissionNames) {
        return requirePermissions(antPath, getPermissions(permissionNames), true);
    }

    /**
     * Configures required permissions for the provided ant path and permission names.
     * The permissions will be retrieved from the permission repository.
     * This allows authorization if the user's role has all the permissions
     * @param antPath the ant path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissions the permissions to require permissions on
     * @return an instance of this for chaining
     */
    public PermissionsConfigurer requireAllPermissions(String antPath, Permission...permissions) {
        return requirePermissions(antPath, Arrays.asList(permissions), true);
    }

    /**
     * Configures required permissions for the provided ant path and permissions. If requireAll is true, the user's role
     * must all be contained in the provided permissions, otherwise only one is required
     * @param antPath the api path to lock with permissions. If it doesn't end in /, it will be added since the matcher expects it
     * @param permissions the permissions required to access this api path
     * @param requireAll true to require all permissions to be satisfied by a user's role, else at least one
     * @return an instance of this for chaining
     */
    public PermissionsConfigurer requirePermissions(String antPath, Collection<Permission> permissions, boolean requireAll) {
        if (!antPath.endsWith("*") && !antPath.endsWith("/"))
            antPath += "/";

        UserAuthorizer.RequiredPermissions requiredPermissions = new UserAuthorizer.RequiredPermissions(permissions, requireAll);
        this.requiredPermissions.put(antPath, requiredPermissions);

        return this;
    }

    /**
     * Builds the authorizer based on the configuration
     * @return the UserAuthorizer
     */
    public UserAuthorizer getAuthorizer() {
        return new UserAuthorizer(this.requiredPermissions);
    }
}
