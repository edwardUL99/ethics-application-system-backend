package ie.ul.ethics.scieng.users.config;

import ie.ul.ethics.scieng.users.authorization.RequestMethod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a listing of configured permissions paths
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionsPathsConfig {
    /**
     * The list of configured paths
     */
    private List<ConfiguredPath> paths = new ArrayList<>();

    /**
     * This class represents a configured path and the required permissions for it
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class ConfiguredPath {
        /**
         * The path to register
         */
        private String path;
        /**
         * The comma separated list of permission names
         */
        @Getter(AccessLevel.NONE)
        private String permissions;
        /**
         * True if all permissions are required or just one
         */
        private boolean requireAll;
        /**
         * The request method to configure this path for. Can be a comma separated list of multiple
         */
        @Getter(AccessLevel.NONE)
        private String requestMethod;

        /**
         * Return all the registered permissions
         * @return the array of registered permissions
         */
        public String[] getPermissions() {
            if (this.permissions == null)
                throw new IllegalStateException("You need to specify permissions in a configured path");

            Collection<String> permissions = Arrays.stream(this.permissions.split(","))
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            String[] array = new String[permissions.size()];

            return permissions.toArray(array);
        }

        /**
         * Returns the list of request methods that can be configured for this path
         * @return the array of request methods to configure the permissions for
         */
        public RequestMethod[] getRequestMethods() {
            if (requestMethod == null) {
                return new RequestMethod[]{RequestMethod.ALL};
            } else {
                Collection<RequestMethod> methods = Arrays.stream(this.requestMethod.split(","))
                        .map(String::trim)
                        .map(RequestMethod::valueOf)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                RequestMethod[] array = new RequestMethod[methods.size()];

                return methods.toArray(array);
            }
        }
    }
}
