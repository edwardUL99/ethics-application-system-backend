package ie.ul.edward.ethics.users.models.roles;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * This class represents a permission
 */
@Entity
public class Permission extends Authorization {
    /**
     * This permission allows a role to create an application
     */
    public static final Permission CREATE_APPLICATION = new Permission(null, "Create Application");

    /**
     * This permission allows a role to edit an application
     */
    public static final Permission EDIT_APPLICATION = new Permission(null, "Edit Application");

    /**
     * This permission allows a role to view their own applications
     */
    public static final Permission VIEW_OWN_APPLICATIONS = new Permission(null, "View Own Applications");

    /**
     * This permission allows a role to view all submitted applications
     */
    public static final Permission VIEW_ALL_APPLICATIONS = new Permission(null, "View All Applications");

    // TODO put more defined roles here

    /**
     * Creates a default Permission object
     */
    public Permission() {
        this(null, null);
    }

    /**
     * Creates a Permission object with the provided ID and name
     * @param id the ID of the permission
     * @param name the name of the permission
     */
    public Permission(Long id, String name) {
        super(id, name);
    }

    /**
     * Check if the provided object matches this permission
     * @param o the object to check equality of
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission permission = (Permission) o;
        return Objects.equals(id, permission.id) && Objects.equals(name, permission.name);
    }

    /**
     * Retrieve the hash code for this permission
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
