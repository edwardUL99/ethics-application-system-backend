package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.models.authorization.Role;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides a listing of the default pre-defined roles in the system.
 *
 * After the system is first started with a database connected, it is a bug condition to change the names of the already
 * defined roles, as this will result in undefined errors and dangling references. You can however, add new roles, with
 * unique names, and these will be added on the next start-up or update the descriptions.
 *
 * <b>New roles must not be final so that they can be updated with persisted entities on startup if necessary, however it is not
 * recommended to change their value otherwise after startup</b
 */
@Log4j2
public final class Roles {
    /**
     * The standard user that has the permissions CREATE_APPLICATION, EDIT_APPLICATION and VIEW_OWN_APPLICATIONS
     */
    public static Role APPLICANT =
        new Role(null, "Applicant",
                "This role is the default role allocated to every new user. New committee members are " +
                        "upgraded from this role by the Chair",
                Arrays.asList(
                Permissions.CREATE_APPLICATION,
                Permissions.EDIT_APPLICATION,
                Permissions.VIEW_OWN_APPLICATIONS
        ));

    /**
     * The role that is assigned to committee members that is not the administrator or chair
     */
    public static Role COMMITTEE_MEMBER =
        new Role(null, "Committee Member",
                "This role is the role allocated to a committee member",
                List.of(
                        Permissions.VIEW_OWN_APPLICATIONS,
                        Permissions.REVIEW_APPLICATIONS
                ));

    /**
     * This role is a role that has all available permissions
     */
    public static Role CHAIR =
        new Role(null, "Chair",
                "This role gives a user access to all defined permissions",
                Permissions.getPermissions(), true, "COMMITTEE_MEMBER");

    /**
     * This is a role that has all the permissions of the CHAIR except for a certain set
     */
    public static Role ADMINISTRATOR =
        new Role(null, "Administrator",
                "This role has the same permissions as the chair except for the permission to grant permissions to others",
                Permissions.getPermissions().stream()
                        .filter(p -> !Objects.equals(Permissions.GRANT_PERMISSIONS, p))
                        .collect(Collectors.toCollection(LinkedHashSet::new)), false, "COMMITTEE_MEMBER");

    /**
     * Get the default declared role objects
     * @return the collection of declared roles
     */
    public static Collection<Role> getRoles() {
        Roles roleObj = new Roles();

        return Arrays.stream(Roles.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && !f.getName().equals("log"))
            .map(f -> {
                if (Modifier.isFinal(f.getModifiers()))
                    throw new IllegalStateException("Role field " + f + " is final. Role fields need to be non-final");

                try {
                    Role r = (Role)f.get(roleObj);
                    r.setTag(f.getName());

                    return r;
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Update the role with the same tag if it exists
     * @param role the tole to update
     */
    public static void updateRole(Role role) {
        Roles roles = new Roles();
        String tag = role.getTag();

        try {
            Field field = Permissions.class.getDeclaredField(tag);
            boolean accessible = field.canAccess(null);

            if (!accessible)
                field.setAccessible(true);

            field.set(roles, role);

            if (!accessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ex) {
            log.warn("Failed to update permission {}", tag);
        }
    }

    /**
     * Get the role by the given tag
     * @param tag the tag identifying the role (same as field name)
     * @return the found role
     */
    public static Role getRole(String tag) {
        return getRoles().stream()
                .filter(r -> r.getTag().equals(tag))
                .findFirst()
                .orElse(null);
    }

    /**
     * Don't allow external instantiation
     */
    private Roles() {}
}
