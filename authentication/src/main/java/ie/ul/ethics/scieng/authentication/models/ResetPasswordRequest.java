package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents a request to reset the user's password
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    /**
     * The username of the account to reset the password with
     */
    @NotNull
    private String username;
    /**
     * The password reset token
     */
    @NotNull
    private String token;
    /**
     * The new password
     */
    @NotNull
    private String password;
}
