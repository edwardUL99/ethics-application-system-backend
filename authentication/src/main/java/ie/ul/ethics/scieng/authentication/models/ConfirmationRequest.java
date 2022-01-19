package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents a request to confirm an account
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationRequest {
    /**
     * The email used for confirmation
     */
    @NotNull
    private String email;
    /**
     * The token used for confirmation
     */
    @NotNull
    private String token;
}
