package ie.ul.ethics.scieng.users.repositories;

import ie.ul.ethics.scieng.users.models.authorization.Role;
import org.springframework.stereotype.Repository;

/**
 * This repository represents the repository for storing and retrieving roles
 */
@Repository
public interface RoleRepository extends AuthorizationRepository<Role> {
}
