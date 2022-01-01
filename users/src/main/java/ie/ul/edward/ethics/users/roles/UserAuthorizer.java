package ie.ul.edward.ethics.users.roles;

import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.models.roles.Permission;
import ie.ul.edward.ethics.users.models.roles.Role;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * This class provides authorization to API operations based on a user's roles/permissions
 */
public class UserAuthorizer {
    /**
     * The map of ANT paths and their required permissions
     */
    private final Map<String, RequiredPermissions> pathPermissions;

    /**
     * The matcher for matching paths
     */
    private static final AntPathMatcher matcher = new AntPathMatcher();

    static {
        matcher.setTrimTokens(true);
    }

    /**
     * Create a UserAuthorizer object with the provided map of ANT paths to required permissions mappings
     * @param pathPermissions the map of path permissions
     */
    public UserAuthorizer(Map<String, RequiredPermissions> pathPermissions) {
        this.pathPermissions = pathPermissions;
    }

    /**
     * For the path given, authorize the user to see if their role grants them permissions required for the path
     * @param path the path to match. If it doesn't end in /, it will be added since the matcher expects it
     * @param user the user to authorize
     * @return true if authorized, false if not. Always true if the path isn't matched. If the user has no role, and the
     * path is matched, false is returned. If the path is matched, and user is null, false is returned
     */
    public boolean authorise(String path, User user) {
        RequiredPermissions requiredPermissions = permissionsRequired(path);

        if (requiredPermissions != null) {
            if (user == null)
                return false;

            Role role = user.getRole();

            if (role == null)
                return false;
            else
                return requiredPermissions.match(role.getPermissions());
        }

        return true;
    }

    /**
     * This method returns the required permissions gor the given path. If the path has been configured to need
     * permissions, it will be matched. Otherwise, null is returned to indicate no permissions are required
     * @param path the path to match. If it doesn't end in /, it will be added since the matcher expects it
     * @return collection of required permissions
     */
    public RequiredPermissions permissionsRequired(String path) {
        if (!path.endsWith("/"))
            path += "/";

        for (Map.Entry<String, RequiredPermissions> e : pathPermissions.entrySet()) {
            String key = e.getKey();

            if (matcher.match(key, path)) {
                return e.getValue();
            }
        }

        return null;
    }

    /**
     * This class holds a list of permissions required for a path
     */
    public static class RequiredPermissions {
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
            Set<Permission> permissionSet = new LinkedHashSet<>(permissions);
            Set<Permission> difference = new LinkedHashSet<>(this.permissions);
            difference.retainAll(permissionSet);

            return (requireAll) ? difference.size() == this.permissions.size():difference.size() > 0;
        }
    }
}
