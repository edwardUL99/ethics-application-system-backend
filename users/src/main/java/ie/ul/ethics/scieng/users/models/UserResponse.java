package ie.ul.ethics.scieng.users.models;

import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The response of requesting loading a user
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserResponse {
    /**
     * The user's username
     */
    private String username;
    /**
     * The user's name
     */
    private String name;
    /**
     * The user's email address
     */
    private String email;
    /**
     * The user's department
     */
    private String department;
    /**
     * The role of the user to include in the response
     */
    private Role role;

    /**
     * This constructor converts the given user into a UserResponse
     * @param user the user to convert
     */
    public UserResponse(User user) {
        Account account = user.getAccount();

        if (account != null) {
            this.username = account.getUsername();
            this.email = account.getEmail();
        }

        this.name = user.getName();
        this.department = user.getDepartment();
        this.role = user.getRole();
    }
}
