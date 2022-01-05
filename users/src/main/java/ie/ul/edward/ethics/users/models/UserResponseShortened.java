package ie.ul.edward.ethics.users.models;

import ie.ul.edward.ethics.authentication.models.Account;
import lombok.*;

/**
 * This class represents a user response containing just user information without role
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponseShortened {
    /**
     * The username of the user
     */
    private String username;
    /**
     * The user's email
     */
    private String email;
    /**
     * The user's name
     */
    private String name;
    /**
     * The department the user is situated in
     */
    private String department;

    /**
     * Creates the shortened response from the provided user
     * @param user the user to create the response from
     */
    public UserResponseShortened(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.department = user.getDepartment();

        Account account = user.getAccount();

        if (account != null) {
            this.email = account.getEmail();
        }
    }
}
