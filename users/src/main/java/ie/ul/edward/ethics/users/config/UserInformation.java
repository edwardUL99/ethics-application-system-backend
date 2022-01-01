package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bean holding information about the authenticated user
 */
@Getter
@Setter
@EqualsAndHashCode
public class UserInformation {
    /**
     * The loaded user
     */
    private User user;

    /**
     * Creates a default user information object
     */
    public UserInformation() {
        this(null);
    }

    /**
     * Create a UserInformation object with the provided user
     * @param user the user to create the object with
     */
    public UserInformation(User user) {
        this.user = user;
    }
}
