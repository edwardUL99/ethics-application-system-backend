package ie.ul.ethics.scieng.users.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This request is used to update a user's role
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdateRoleRequest {
    /**
     * The username of the user to update the role of
     */
    @NotNull
    private String username;
    /**
     * The role ID
     */
    @NotNull
    private Long role;
}
