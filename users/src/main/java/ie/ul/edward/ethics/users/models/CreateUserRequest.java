package ie.ul.edward.ethics.users.models;

import com.sun.istack.NotNull;
import lombok.*;

/**
 * This class represents a request to create a user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateUserRequest {
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
