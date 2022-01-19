package ie.ul.ethics.scieng.users.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a container that is intended to be a request scoped bean which contains the user that has been
 * passed through permissions authorization
 */
@Getter
@Setter
@EqualsAndHashCode
public class AuthorizedUser {
    /**
     * The user that has been authorized to access the operation/resource
     */
    private User user;

    /**
     * Creates a default AuthorizedUser object
     */
    public AuthorizedUser() {
        this(null);
    }

    /**
     * Creates an AuthorizedUser object initialised with the provided user
     * @param user the user to initialise the object with
     */
    public AuthorizedUser(User user) {
        this.user = user;
    }
}
