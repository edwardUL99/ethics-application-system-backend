package ie.ul.edward.ethics.users.repositories;

import ie.ul.edward.ethics.users.models.authorization.Permission;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for storing and retrieving permissions
 */
@Repository
public interface PermissionRepository extends AuthorizationRepository<Permission> {
}
