package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.config.UserPermissionsConfig;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class holds a list of permissions required for a path
 */
@Getter
@EqualsAndHashCode
public class RequiredPermissions {
    /**
     * The set of required permissions
     */
    private final Set<Permission> permissions;
    /**
     * A flag indicating that all permissions are required
     */
    private final boolean requireAll;

    /**
     * Create RequiredPermissions object with the list of permissions and require all set to false
     * @param permissions the permissions required
     */
    public RequiredPermissions(Permission...permissions) {
        this(Arrays.asList(permissions), false);
    }

    /**
     * Create RequiredPermissions object with the given permissions and requireAll parameter
     * @param permissions the collection of permissions
     * @param requireAll true if permissions are only authorized if the role has all the permissions or false if at
     *                   least one is required
     */
    public RequiredPermissions(Collection<Permission> permissions, boolean requireAll) {
        this.permissions = new LinkedHashSet<>(permissions);
        this.requireAll = requireAll;
    }

    /**
     * Returns true if the permissions given satisfy the required permissions of this object
     * @param permissions the permissions given
     * @return true if satisfied, false if not
     */
    public boolean match(Collection<Permission> permissions) {
        if (UserPermissionsConfig.permissionsDisabled())
            return true; // always return true if disabled

        Set<Permission> permissionSet = new LinkedHashSet<>(permissions);
        Set<Permission> difference = new LinkedHashSet<>(this.permissions);
        difference.retainAll(permissionSet);

        return (requireAll) ? difference.size() == this.permissions.size():difference.size() > 0;
    }
}