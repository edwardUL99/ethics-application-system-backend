package ie.ul.edward.ethics.users.authorization;

import ie.ul.edward.ethics.users.models.authorization.Permission;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides a list of permissions in the system
 */
public final class Permissions {
    /**
     * This permission allows a role to create an application
     */
    public static final Permission CREATE_APPLICATION =
            new Permission(null, "Create Application", "This permission allows a user to create and submit an application");

    /**
     * This permission allows a role to edit an application
     */
    public static final Permission EDIT_APPLICATION =
            new Permission(null, "Edit Application", "This permission allows a user to edit an application");

    /**
     * This permission allows a role to view their own applications
     */
    public static final Permission VIEW_OWN_APPLICATIONS =
            new Permission(null, "View Own Applications", "This permission allows a user to view applications submitted by them");

    /**
     * This permission allows a role to view all submitted applications
     */
    public static final Permission VIEW_ALL_APPLICATIONS =
            new Permission(null, "View All Applications", "This permission allows a user to view all applications in the system");

    // TODO put more defined roles here

    /**
     * This permission allows a role to create new users
     */
    public static final Permission CREATE_USERS =
            new Permission(null, "Create Users", "This permission allows a user to create new users");

    /**
     * This permission allows a role to delete other users. It should not be allowed to delete administrators however,
     * this should be left up to the business logic to determine
     */
    public static final Permission DELETE_USERS =
            new Permission(null, "Delete Users", "This permission allows a user to delete other users");

    /**
     * This permission allows a role to grant permissions to another user.
     * It also allows the creation and allocation of roles
     */
    public static final Permission GRANT_PERMISSIONS =
            new Permission(null, "Grant Permissions", "This permission allows a user to grant permissions and allocate roles to other users");

    /**
     * Retrieve all the defined permissions
     * @return collection of defined permissions
     */
    public static Collection<Permission> getPermissions() {
        Permissions permissionObj = new Permissions();

        return Arrays.stream(Permissions.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))
                .map(f -> {
                    try {
                        return (Permission) f.get(permissionObj);
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Don't allow external instantiation
     */
    private Permissions() {}
}
