package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.models.authorization.Permission;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides a list of permissions in the system
 *
 * After the system is first started with a database connected, it is a bug condition to change the names of the already
 * defined permissions, as this will result in undefined errors and dangling references. You can however, add new roles, with
 * unique names, and these will be added on the next start-up or update the descriptions
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
     * This permission allows a role to view their own applications if an applicant, else if a committee member, applications that may be assigned
     * to them
     */
    public static final Permission VIEW_OWN_APPLICATIONS =
            new Permission(null, "View Own Applications", "This permission allows a user to view applications submitted by them " +
                    "or in the case of committee members, applications assigned to them");

    /**
     * This permission allows a role to view all submitted applications
     */
    public static final Permission VIEW_ALL_APPLICATIONS =
            new Permission(null, "View All Applications", "This permission allows a user to view all applications in the system");

    /**
     * This permission allows a role to review applications
     */
    public static final Permission REVIEW_APPLICATIONS =
            new Permission(null, "Review Applications", "This permission allows a user to review applications");

    /**
     * This permission allows a role to refer applications back to applicants
     */
    public static final Permission REFER_APPLICATIONS =
            new Permission(null, "Refer Applications", "This permission allows a user to refer applications back to the applicant");

    /**
     * This permission allows a role to view all admin resources
     */
    public static final Permission ADMIN =
            new Permission(null, "Admin", "This permission grants a user access to certain admin resources");

    /**
     * This permission allows a role to approve applications
     */
    public static final Permission APPROVE_APPLICATIONS =
            new Permission(null, "Approve Applications", "This permission grants a user the ability to approve applications");

    /**
     * This permission allows a role to assign applications to committee members
     */
    public static final Permission ASSIGN_APPLICATIONS =
            new Permission(null, "Assign Applications", "This permission grants a user the ability to assign apploications to committee members");

    /**
     * This permission allows a role to create new users
     */
    public static final Permission CREATE_USERS =
            new Permission(null, "Create Users", "This permission allows a user to create new users");

    // TODO need to add functionality to create and delete users to controller and user service

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
                        Permission p = (Permission) f.get(permissionObj);
                        p.setTag(f.getName());

                        return p;
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Retrieve the permission that matches the provided field name (e.g. CREATE_APPLICATION)
     * @param fieldName the field name
     * @return the found permission, null if not found
     */
    public static Permission getPermissionByFieldName(String fieldName) {
        Permissions permissionObj = new Permissions();

        return Arrays.stream(Permissions.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()) &&
                        f.getName().equals(fieldName))
                .map(f -> {
                    try {
                        return (Permission) f.get(permissionObj);
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Don't allow external instantiation
     */
    private Permissions() {}
}
