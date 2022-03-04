package ie.ul.ethics.scieng.users.models.authorization;

import javax.persistence.*;
import java.util.*;

/**
 * A role represents a set of permissions with a given name
 */
@Entity
@Table(name="UserRoles")
public class Role extends Authorization {
    /**
     * The collection of permissions belonging to this role
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private final Collection<Permission> permissions;
    /**
     * This field determines if the role is only allowed to be assigned to a single user or not
     */
    private boolean singleUser;

    /**
     * Create a default role
     */
    public Role() {
        this(null, null, null, new LinkedHashSet<>());
    }

    /**
     * Create a role with the provided ID, name and permissions, with singleUser set to false
     * @param id the ID of the role
     * @param name the name of the role
     * @param description a short description of this role
     * @param permissions the permissions this role contains
     */
    public Role(Long id, String name, String description, Collection<Permission> permissions) {
        this(id, name, description, permissions, false);
    }

    /**
     * Create a role with the provided ID, name and permissions, and singleUser
     * @param id the ID of the role
     * @param name the name of the role
     * @param description a short description of this role
     * @param permissions the permissions this role contains
     * @param singleUser true if the role is only allowed to be assigned to a single user at a time
     */
    public Role(Long id, String name, String description, Collection<Permission> permissions, boolean singleUser) {
        super(id, name, description);
        this.permissions = new LinkedHashSet<>(permissions);
        this.singleUser = singleUser;
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
     * Add all the permissions in the given collection to this role
     * @param permissions the permissions to add
     */
    public void addAllPermissions(Collection<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    /**
     * Remove the permission from the role
     * @param permission the permission to remove
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * Remove all the permissions in the provided collection from this role
     * @param permissions the permissions to remove
     */
    public void removeAllPermissions(Collection<Permission> permissions) {
        this.permissions.removeAll(permissions);
    }

    /**
     * Returns true if this role is for only one user, else false
     * @return true if role is allocated to only 1 user
     */
    public boolean isSingleUser() {
        return singleUser;
    }

    /**
     * Set the value of singleUser
     * @param singleUser true if this role is to be for only a single user, false if not
     */
    public void setSingleUser(boolean singleUser) {
        this.singleUser = singleUser;
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
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(permissions, role.permissions)
                && Objects.equals(singleUser, role.singleUser);
    }

    /**
     * Retrieve the hash code for this role
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, permissions, singleUser);
    }
}
