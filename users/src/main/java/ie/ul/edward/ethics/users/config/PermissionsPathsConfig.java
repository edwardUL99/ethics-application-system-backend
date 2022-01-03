package ie.ul.edward.ethics.users.config;

import lombok.*;

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
         * The request method to configure this for
         */
        private String requestMethod;

        /**
         * Return all the registered permissions
         * @return the array of registered permissions
         */
        public String[] getPermissions() {
            Collection<String> permissions = Arrays.stream(this.permissions.split(","))
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            String[] array = new String[permissions.size()];

            return permissions.toArray(array);
        }
    }
}
