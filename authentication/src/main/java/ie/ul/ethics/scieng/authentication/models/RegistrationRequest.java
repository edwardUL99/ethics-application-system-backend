package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * This class represents a registration request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RegistrationRequest {
    /**
     * The account's username
     */
    @NotNull
    private String username;
    /**
     * The account's email address
     */
    @Email
    @NotNull(message = "An email must be provided with an account")
    private String email;
    /**
     * The account's password
     */
    @NotNull
    private String password;
    /**
     * A key that can be used to always confirm if confirmation is enabled
     */
    private String confirmationKey;

    /**
     * Create a registration request from the provided account
     * @param account the account to create the request from
     */
    public RegistrationRequest(Account account) {
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.password = account.getPassword();
    }
}
