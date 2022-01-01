package ie.ul.edward.ethics.users.models.roles;

import javax.persistence.*;

/**
 * This class represents an object that performs some functionality in ensuring a user is authorized to perform an
 * operation. An example is a Role which is a set of permissions and a Permission.
 */
@Entity
public abstract class Authorization {
    /**
     * The ID of the authorization
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * The name of the authorization object
     */
    @Column(unique = true, length = 128)
    protected String name;

    /**
     * Creates a default authorization object
     */
    protected Authorization() {
        this(null, null);
    }

    /**
     * Creates an authorization object with the provided ID and name
     * @param id the ID for this authorization object
     * @param name the name of the authorization object
     */
    protected Authorization(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieve the ID of this object
     * @return JPA database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the ID of the object
     * @param id the new ID to use
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieve the name of the object
     * @return the object name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the new name of the object
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
}
