package ie.ul.ethics.scieng.users.models.authorization;

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
    @ManyToMany(fetch = FetchType.EAGER)
    private final Collection<Permission> permissions;
    /**
     * This field determines if the role is only allowed to be assigned to a single user or not
     */
    private boolean singleUser;
    /**
     * The role to downgrade users to if singleUser is true and users need to be downgraded
     */
    private String downgradeTo;

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
        this(id, name, description, permissions, false, null);
    }

    /**
     * Create a role with the provided ID, name and permissions, and singleUser
     * @param id the ID of the role
     * @param name the name of the role
     * @param description a short description of this role
     * @param permissions the permissions this role contains
     * @param singleUser true if the role is only allowed to be assigned to a single user at a time
     * @param downgradeTo the tag of the role to downgrade users to if singleUser is true
     */
    public Role(Long id, String name, String description, Collection<Permission> permissions, boolean singleUser, String downgradeTo) {
        super(id, name, description);
        this.permissions = new LinkedHashSet<>(permissions);
        this.singleUser = singleUser;
        this.downgradeTo = downgradeTo;

        if (this.singleUser && this.downgradeTo == null)
            throw new IllegalArgumentException("If singleUser is true, downgradeTo must not be null");
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
     * Get the tag of the role to downgrade the user to
     * @return the tag of the role to downgrade to
     */
    public String getDowngradeTo() {
        return downgradeTo;
    }

    /**
     * Set the role to downgrade the user to
     * @param downgradeTo the new tag of the role to downgrade to
     */
    public void setDowngradeTo(String downgradeTo) {
        this.downgradeTo = downgradeTo;
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
        return Objects.equals(name, role.name) && Objects.equals(description, role.description) && Objects.equals(permissions, role.permissions)
                && Objects.equals(singleUser, role.singleUser) && Objects.equals(downgradeTo, role.downgradeTo);
    }

    /**
     * Retrieve the hash code for this role
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description, permissions, singleUser, downgradeTo);
    }
}
