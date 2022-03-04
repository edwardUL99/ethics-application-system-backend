package ie.ul.ethics.scieng.users.models.authorization;

import javax.persistence.*;

/**
 * This class represents an authorization to a user to access a certain resource, for example an API endpoint. It is a
 * means of determining (at a more granular level than password authentication), the resources a user can access/operations they can
 * perform based on the authorization they have been granted.
 *
 * Authorization is primarily carried out through the use of verifying a user has the required permissions to access the resource.
 * To make it easier to assign multiple permissions to a user, a role can be defined with one or more permissions and
 * allocated to more than one user.
 */
@Entity
@Table(name="UserAuthorizations")
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
     * A short description of the authorization. Excluded from equals and hash code
     */
    protected String description;
    /**
     * The "tag" name to identify the role programmatically. Usually equals the name of the constant declared in a list of authorisations
     */
    protected String tag;

    /**
     * Creates a default authorization object
     */
    protected Authorization() {
        this(null, null, null);
    }

    /**
     * Creates an authorization object with the provided ID and name
     * @param id the ID for this authorization object
     * @param name the name of the authorization object
     * @param description a short description of this authorization
     */
    protected Authorization(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    /**
     * Retrieve the description for this authorization
     * @return the authorization description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Change the authorization's description
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the "tag" name to identify the role programmatically. Usually equals the name of the constant declared in a list of authorisations
     * @param tag the new tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Retrieve the tag name
     * @return the tag
     */
    public String getTag() {
        return tag;
    }
}
