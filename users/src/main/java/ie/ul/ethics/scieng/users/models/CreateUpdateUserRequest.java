package ie.ul.ethics.scieng.users.models;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents a request to create a user. Can also be used for updating users
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateUpdateUserRequest {
    /**
     * The username that matches the account that should already exist
     */
    @NotNull
    private String username;
    /**
     * The name of the user to create
     */
    @NotNull
    private String name;
    /**
     * The department the user belongs to
     */
    @NotNull
    private String department;
}
