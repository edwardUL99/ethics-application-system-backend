package ie.ul.edward.ethics.users.repositories;

import ie.ul.edward.ethics.users.models.roles.Authorization;
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
     * Determine if the authorization exists by name
     * @param name the name of the object
     * @return true if it exists, false if not
     */
    boolean existsByName(String name);
}
