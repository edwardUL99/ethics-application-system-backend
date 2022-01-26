package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.config.UserPermissionsConfig;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * This class provides authorization to API operations based on a user's roles/permissions
 */
public class PermissionsAuthorizer {
    /**
     * The map of ANT paths and their required permissions
     */
    private final Map<Path, RequiredPermissions> pathPermissions;

    /**
     * The matcher for matching paths
     */
    private static final AntPathMatcher matcher = new AntPathMatcher();

    static {
        matcher.setTrimTokens(true);
    }

    /**
     * Create a PermissionsAuthorizer object with the provided map of ANT paths to required permissions mappings
     * @param pathPermissions the map of path permissions
     */
    public PermissionsAuthorizer(Map<Path, RequiredPermissions> pathPermissions) {
        this.pathPermissions = pathPermissions;
    }

    /**
     * For the path given, authorize the user to see if their role grants them permissions required for the path
     * @param path the path to match. If it doesn't end in /, it will be added since the matcher expects it
     * @param method the request method
     * @param user the user to authorize
     * @return true if authorized, false if not. Always true if the path isn't matched. If the user has no role, and the
     * path is matched, false is returned. If the path is matched, and user is null, false is returned
     */
    public boolean authorise(String path, String method, User user) {
        if (UserPermissionsConfig.permissionsDisabled())
            return true; // always authorise if disabled

        Map<String, List<RequiredPermissions>> requiredPermissions = permissionsRequired(path);
        List<RequiredPermissions> matchedPermissionsList = requiredPermissions.get(method);

        if (matchedPermissionsList == null && requiredPermissions.containsKey("ALL"))
            matchedPermissionsList = requiredPermissions.get("ALL");

        if (matchedPermissionsList != null) {
            boolean authorized = true;

            for (RequiredPermissions matchedPermissions : matchedPermissionsList) {
                if (user == null)
                    return false;

                if (matchedPermissions != null) {
                    Role role = user.getRole();

                    if (role == null)
                        return false;
                    else
                        authorized = authorized && matchedPermissions.match(role.getPermissions());
                }
            }

            return authorized;
        }

        return true;
    }

    /**
     * This method returns the required permissions gor the given path. If the path has been configured to need
     * permissions, it will be matched. Otherwise, null is returned to indicate no permissions are required.
     * If the path matches multiple configurations, these will be returned
     * @param path the path to match. If it doesn't end in /, it will be added since the matcher expects it
     * @return map of request methods to the list request permissions that match this path
     */
    public Map<String, List<RequiredPermissions>> permissionsRequired(String path) {
        Map<String, List<RequiredPermissions>> permissionsMap = new HashMap<>();

        if (!path.endsWith("/"))
            path += "/";

        for (Map.Entry<Path, RequiredPermissions> e : pathPermissions.entrySet()) {
            Path key = e.getKey();

            String requestMethod = key.requestMethod.toString();
            List<RequiredPermissions> requiredPermissions = permissionsMap.computeIfAbsent(requestMethod, k -> new ArrayList<>());

            if (matcher.match(key.path, path))
                requiredPermissions.add(e.getValue());
        }

        permissionsMap.values().removeIf(v -> v.size() == 0);

        return permissionsMap;
    }

    /**
     * This class represents a path that is to be locked by permissions and the request method used to access that path
     */
    @Getter
    @EqualsAndHashCode
    public static class Path {
        /**
         * The path (can be in ANT pattern) that needs to be matched by the request
         */
        private final String path;
        /**
         * The request method matched by this path
         */
        private final RequestMethod requestMethod;

        /**
         * Creates a Path object that registers permissions for all request methods.
         * @param path the path to register
         */
        public Path(String path) {
            this(path, RequestMethod.ALL);
        }

        /**
         * Creates a Path object that registers permissions for the path and specified request method.
         * @param path the path to register the permissions for
         * @param requestMethod the request method to specify permissions for
         */
        public Path(String path, RequestMethod requestMethod) {
            this.path = path;
            this.requestMethod = (requestMethod == null) ? RequestMethod.ALL:requestMethod;
        }
    }
}
