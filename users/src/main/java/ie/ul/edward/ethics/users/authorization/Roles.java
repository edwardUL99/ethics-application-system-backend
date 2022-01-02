package ie.ul.edward.ethics.users.authorization;

import ie.ul.edward.ethics.users.models.authorization.Role;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides a listing of the default pre-defined roles in the system
 */
public final class Roles {
    /**
     * The standard user that has the permissions CREATE_APPLICATION, EDIT_APPLICATION and VIEW_OWN_APPLICATIONS
     */
    public static final Role STANDARD_USER = new Role();

    // todo add more default roles here

    /**
     * This role is a role that has all available permissions
     */
    public static final Role CHAIR = new Role();

    /**
     * This is a role that has all the permissions of the CHAIR except for a certain set
     */
    public static final Role ADMINISTRATOR = new Role();

    static {
        STANDARD_USER.setName("Standard User");
        STANDARD_USER.addPermission(Permissions.CREATE_APPLICATION);
        STANDARD_USER.addPermission(Permissions.EDIT_APPLICATION);
        STANDARD_USER.addPermission(Permissions.VIEW_OWN_APPLICATIONS);
        STANDARD_USER.setDescription("This role is the default role allocated to every new user");

        CHAIR.setName("Chair");
        CHAIR.addAllPermissions(Permissions.getPermissions());
        CHAIR.setDescription("This role gives a user access to all defined permissions");

        ADMINISTRATOR.setName("Administrator");
        ADMINISTRATOR.addAllPermissions(Permissions.getPermissions());
        ADMINISTRATOR.removeAllPermissions(Collections.singletonList(Permissions.GRANT_PERMISSIONS)); // sub administrators (e.g, secretary) aren't allowed grant permissions
        ADMINISTRATOR.setDescription("This role has the same permissions as the chair, but is not allowed grant permissions to users");
    }

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
