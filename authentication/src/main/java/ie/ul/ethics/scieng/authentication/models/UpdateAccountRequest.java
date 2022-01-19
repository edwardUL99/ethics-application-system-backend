package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This request is used for when updating an account
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdateAccountRequest {
    /**
     * The username of the account to update
     */
    @NotNull
    private String username;
    /**
     * The password to update
     */
    @NotNull
    private String password;
}
