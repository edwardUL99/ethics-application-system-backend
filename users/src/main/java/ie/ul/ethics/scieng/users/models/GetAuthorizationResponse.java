package ie.ul.ethics.scieng.users.models;

import ie.ul.ethics.scieng.users.models.authorization.Authorization;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
