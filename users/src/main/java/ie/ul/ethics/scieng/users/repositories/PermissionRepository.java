package ie.ul.ethics.scieng.users.repositories;

import ie.ul.ethics.scieng.users.models.authorization.Permission;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for storing and retrieving permissions
 */
@Repository
public interface PermissionRepository extends AuthorizationRepository<Permission> {
}
