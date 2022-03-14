package ie.ul.ethics.scieng.users.repositories;

import ie.ul.ethics.scieng.users.models.authorization.Authorization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * This repository provides access to any authorization objects
 */
@NoRepositoryBean
public interface AuthorizationRepository<T extends Authorization> extends CrudRepository<T, Long> {
    /**
     * Find the authorization object by name
     * @param name the name of the object
     * @return the found object if found, or else an empty optional
     */
    Optional<T> findByName(String name);

    /**
     * Find by the tag name
     * @param tag the tag (field name)
     * @return the optional containing the authorization
     */
    Optional<T> findByTag(String tag);
}
