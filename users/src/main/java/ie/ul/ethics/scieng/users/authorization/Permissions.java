package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.models.authorization.Permission;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
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
 *
 * <b>New permissions must not be final so that they can be updated with persisted entities on startup if necessary, however it is not
 * recommended to change their value otherwise after startup</b
 */
@Log4j2
public final class Permissions {
    /**
     * This permission allows a role to create an application
     */
    public static Permission CREATE_APPLICATION =
            new Permission(null, "Create Application", "This permission allows a user to create and submit an application");

    /**
     * This permission allows a role to edit an application
     */
    public static Permission EDIT_APPLICATION =
            new Permission(null, "Edit Application", "This permission allows a user to edit an application");

    /**
     * This permission allows a role to view their own applications if an applicant, else if a committee member, applications that may be assigned
     * to them
     */
    public static Permission VIEW_OWN_APPLICATIONS =
            new Permission(null, "View Own Applications", "This permission allows a user to view applications submitted by them " +
                    "or in the case of committee members, applications assigned to them");

    /**
     * This permission allows a role to view all submitted applications
     */
    public static Permission VIEW_ALL_APPLICATIONS =
            new Permission(null, "View All Applications", "This permission allows a user to view all applications in the system");

    /**
     * This permission allows a role to review applications
     */
    public static Permission REVIEW_APPLICATIONS =
            new Permission(null, "Review Applications", "This permission allows a user to review applications");

    /**
     * This permission allows a role to refer applications back to applicants
     */
    public static Permission REFER_APPLICATIONS =
            new Permission(null, "Refer Applications", "This permission allows a user to refer applications back to the applicant");

    /**
     * This permission allows a role to view all admin resources
     */
    public static Permission ADMIN =
            new Permission(null, "Admin", "This permission grants a user access to certain admin resources");

    /**
     * This permission allows a role to approve applications
     */
    public static Permission APPROVE_APPLICATIONS =
            new Permission(null, "Approve Applications", "This permission grants a user the ability to approve applications");

    /**
     * This permission allows a role to assign applications to committee members
     */
    public static Permission ASSIGN_APPLICATIONS =
            new Permission(null, "Assign Applications", "This permission grants a user the ability to assign applications to committee members");

    /**
     * This permission allows a role to grant permissions to another user.
     * It also allows the creation and allocation of roles
     */
    public static Permission GRANT_PERMISSIONS =
            new Permission(null, "Grant Permissions", "This permission allows a user to grant permissions and allocate roles to other users");

    /**
     * A permission to allow users to export applications
     */
    public static Permission EXPORT_APPLICATIONS =
            new Permission(null, "Export Applications", "Allows a user to export applications");

    /**
     * Retrieve all the defined permissions
     * @return collection of defined permissions
     */
    public static Collection<Permission> getPermissions() {
        Permissions permissionObj = new Permissions();

        return Arrays.stream(Permissions.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && !f.getName().equals("log"))
            .map(f -> {
                if (Modifier.isFinal(f.getModifiers()))
                    throw new IllegalStateException("Permission field " + f + " is final. Permission fields need to be non-final");

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
        return getPermissions()
                .stream()
                .filter(p -> p.getTag().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update the permission with the same tag if it exists
     * @param permission the permission to update
     */
    public static void updatePermission(Permission permission) {
        Permissions permissions = new Permissions();
        String tag = permission.getTag();

        try {
            Field field = Permissions.class.getDeclaredField(tag);
            boolean accessible = field.canAccess(null);

            if (!accessible)
                field.setAccessible(true);

            field.set(permissions, permission);

            if (!accessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ex) {
            log.warn("Failed to update permission {}", tag);
        }
    }

    /**
     * Don't allow external instantiation
     */
    private Permissions() {}
}
