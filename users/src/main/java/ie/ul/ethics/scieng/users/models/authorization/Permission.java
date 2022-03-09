package ie.ul.ethics.scieng.users.models.authorization;

import javax.persistence.Entity;
import java.util.*;

/**
 * A permission is the smallest unit that represents an Authorization. It can be used to lock access to one or more related
 * resources where the user must possess that permission to access it.
 */
@Entity
public class Permission extends Authorization {
    /**
     * Creates a default Permission object
     */
    public Permission() {
        this(null, null, null);
    }

    /**
     * Creates a Permission object with the provided ID and name
     * @param id the ID of the permission
     * @param name the name of the permission
     * @param description a short description of this permission
     */
    public Permission(Long id, String name, String description) {
        super(id, name, description);
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
        return Objects.equals(name, permission.name) && Objects.equals(description, permission.description);
    }

    /**
     * Retrieve the hash code for this permission
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
