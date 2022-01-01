package ie.ul.edward.ethics.users.models.roles;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

/*
TODO will need to create an object that defines a set of permissions/roles required and authorize(User) method which checks the user's role and therefore, permissions to see if that user can perform the operation

Example:
An interface OperationAuthorization with method authorize(User) could be defined.
One implementation could define anyone with a specified role can access it,
I.e. Standard User or chair can perform the operation

Other implementation could define a set of permissions required to perform the operation.
The user's role is then used to check the permissions that they have
 */

/**
 * A role represents a set of permissions with a given name
 */
@Entity
public class Role extends Authorization {
    /**
     * The collection of permissions belonging to this role
     * TODO may be able to use composite pattern and allow roles contain sub-roles Collection of Authorization objects instead
     */
    @OneToMany
    private final Collection<Permission> permissions;

    /**
     * The standard user that has the permissions CREATE_APPLICATION, EDIT_APPLICATION and VIEW_OWN_APPLICATIONS
     */
    public static final Role STANDARD_USER = new Role();

    // todo add more default roles here

    static {
        STANDARD_USER.setName("Standard User");
        STANDARD_USER.addPermission(Permission.CREATE_APPLICATION);
        STANDARD_USER.addPermission(Permission.EDIT_APPLICATION);
        STANDARD_USER.addPermission(Permission.VIEW_OWN_APPLICATIONS);
    }

    /**
     * Create a default role
     */
    public Role() {
        this(null, null, new LinkedHashSet<>());
    }

    /**
     * Create a role with the provided ID, name and permissions
     * @param id the ID of the role
     * @param name the name of the role
     * @param permissions the permissions this role contains
     */
    public Role(Long id, String name, Collection<Permission> permissions) {
        super(id, name);
        this.permissions = permissions;
    }

    /**
     * Retrieves an immutable view of the role's permissions
     * @return immutable view of role's permissions
     */
    public Collection<Permission> getPermissions() {
        return Collections.unmodifiableCollection(permissions);
    }

    /**
     * Add the provided permission to the Role
     * @param permission the permission to add
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Remove the permission from the role
     * @param permission the permission to remove
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * Check if the provided object matches this role
     * @param o the object to check equality of
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(permissions, role.permissions);
    }

    /**
     * Retrieve the hash code for this role
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, permissions);
    }
}
