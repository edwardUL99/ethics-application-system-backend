package ie.ul.edward.ethics.users.models.authorization;

import javax.persistence.*;
import java.util.*;

/**
 * A role represents a set of permissions with a given name
 */
@Entity
public class Role extends Authorization {
    /**
     * The collection of permissions belonging to this role
     */
    @ManyToMany
    private final Collection<Permission> permissions;

    /**
     * Create a default role
     */
    public Role() {
        this(null, null, null, new LinkedHashSet<>());
    }

    /**
     * Create a role with the provided ID, name and permissions
     * @param id the ID of the role
     * @param name the name of the role
     * @param description a short description of this role
     * @param permissions the permissions this role contains
     */
    public Role(Long id, String name, String description, Collection<Permission> permissions) {
        super(id, name, description);
        this.permissions = new LinkedHashSet<>(permissions);
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