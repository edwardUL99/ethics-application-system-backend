package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.models.authorization.Role;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides a listing of the default pre-defined roles in the system.
 *
 * After the system is first started with a database connected, it is a bug condition to change the names of the already
 * defined roles, as this will result in undefined errors and dangling references. You can however, add new roles, with
 * unique names, and these will be added on the next start-up or update the descriptions
 */
public final class Roles {
    /**
     * The standard user that has the permissions CREATE_APPLICATION, EDIT_APPLICATION and VIEW_OWN_APPLICATIONS
     */
    public static final Role APPLICANT =
            new Role(null, "Applicant",
                    "This role is the default role allocated to every new user. New committee members are " +
                            "upgraded from this role by the Chair",
                    Arrays.asList(
                    Permissions.CREATE_APPLICATION,
                    Permissions.EDIT_APPLICATION,
                    Permissions.VIEW_OWN_APPLICATIONS
            ));

    // todo add more default roles here

    /**
     * The role that is assigned to committee members that is not the administrator or chair
     */
    public static final Role COMMITTEE_MEMBER =
            new Role(null, "Committee Member",
                    "This role is the role allocated to a committee member",
                    List.of(
                            Permissions.VIEW_OWN_APPLICATIONS,
                            Permissions.REVIEW_APPLICATIONS
                    )); // TODO create permissions for committee members

    /**
     * This role is a role that has all available permissions
     */
    public static final Role CHAIR =
            new Role(null, "Chair",
                    "This role gives a user access to all defined permissions",
                    Permissions.getPermissions(), true);

    /**
     * This is a role that has all the permissions of the CHAIR except for a certain set
     */
    public static final Role ADMINISTRATOR =
            new Role(null, "Administrator",
                    "This role has the same permissions as the chair except for the permission to grant permissions to others",
                    Permissions.getPermissions().stream()
                            .filter(p -> !Objects.equals(Permissions.GRANT_PERMISSIONS, p))
                            .collect(Collectors.toCollection(LinkedHashSet::new)), true);

    /**
     * Get the default declared role objects
     * @return the collection of declared roles
     */
    public static Collection<Role> getRoles() {
        Roles roleObj = new Roles();

        return Arrays.stream(Roles.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))
                .map(f -> {
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
     * Don't allow external instantiation
     */
    private Roles() {}
}
