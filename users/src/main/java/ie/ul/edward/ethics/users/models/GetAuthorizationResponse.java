package ie.ul.edward.ethics.users.models;

import ie.ul.edward.ethics.users.models.authorization.Authorization;
import lombok.*;

import java.util.Collection;

/**
 * This response contains all the authorization objects in the system
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GetAuthorizationResponse<T extends Authorization> {
    /**
     * The collection of authorization objects in the response
     */
    private Collection<T> authorizations;
}