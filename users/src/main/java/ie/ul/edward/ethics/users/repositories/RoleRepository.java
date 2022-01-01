package ie.ul.edward.ethics.users.repositories;

import ie.ul.edward.ethics.users.models.roles.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository represents the repository for storing and retrieving roles
 */
@Repository
public interface RoleRepository extends AuthorizationRepository<Role> {
}
