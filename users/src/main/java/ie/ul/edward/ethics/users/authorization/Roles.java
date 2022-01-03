package ie.ul.edward.ethics.users.authorization;

import ie.ul.edward.ethics.users.models.authorization.Role;
import org.springframework.security.core.parameters.P;

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
    public static final Role STANDARD_USER =
            new Role(null, "Standard User",
                    "This role is the default role allocated to every new user",
                    Arrays.asList(
                    Permissions.CREATE_APPLICATION,
                    Permissions.EDIT_APPLICATION,
                    Permissions.VIEW_OWN_APPLICATIONS
            ));

    // todo add more default roles here

    /**
     * This role is a role that has all available permissions
     */
    public static final Role CHAIR =
            new Role(null, "Chair",
                    "This role gives a user access to all defined permissions",
                    Permissions.getPermissions());

    /**
     * This is a role that has all the permissions of the CHAIR except for a certain set
     */
    public static final Role ADMINISTRATOR =
            new Role(null, "Administrator",
                    "This role has the same permissions as the chair except for the permission to grant permissions to others",
                    Permissions.getPermissions().stream()
                            .filter(p -> !Objects.equals(Permissions.GRANT_PERMISSIONS, p))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));

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
                        return (Role)f.get(roleObj);
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
