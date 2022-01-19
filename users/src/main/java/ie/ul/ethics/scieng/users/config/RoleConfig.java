package ie.ul.ethics.scieng.users.config;

import ie.ul.ethics.scieng.users.models.authorization.Permission;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.repositories.PermissionRepository;
import ie.ul.ethics.scieng.users.repositories.RoleRepository;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.authorization.Roles;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This config loads default permissions and roles into the respective repositories
 */
@Component
@Log4j2
public class RoleConfig implements CommandLineRunner {
    /**
     * The repository for saving and loading roles
     */
    private final RoleRepository roleRepository;

    /**
     * The repository for saving and loading permissions
     */
    private final PermissionRepository permissionRepository;

    /**
     * Create a RoleConfig object
     * @param roleRepository the repository for saving and loading roles
     * @param permissionRepository the repository for saving and loading permissions
     * @param userPermissionsConfig the config for user permissions authorization
     */
    @Autowired
    public RoleConfig(RoleRepository roleRepository, PermissionRepository permissionRepository, UserPermissionsConfig userPermissionsConfig) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;

        if (!userPermissionsConfig.isEnabled())
            System.setProperty(UserPermissionsConfig.USER_PERMISSIONS_DISABLED, "true");
    }

    /**
     * Save the permission if it doesn't exist or update if it changed
     * @param permission the permission to save
     */
    private void savePermissionIfNotExists(Permission permission) {
        Permission foundPermission = permissionRepository.findByName(permission.getName()).orElse(null);

        if (foundPermission == null || !foundPermission.equals(permission)) {
            if (foundPermission != null)
                permission.setId(foundPermission.getId());

            permissionRepository.save(permission);
        } else {
            permission.setId(foundPermission.getId());
        }
    }

    /**
     * Save the role if not exists or update if it changed
     * @param role the role to save
     */
    private void saveRoleIfNotExists(Role role) {
        Role foundRole = roleRepository.findByName(role.getName()).orElse(null);

        if (foundRole == null || !foundRole.equals(role)) {
            if (foundRole != null)
                role.setId(foundRole.getId());

            roleRepository.save(role);
        } else {
            role.setId(foundRole.getId());
        }
    }

    /**
     * Save the default permissions
     */
    private void saveDefaultPermissions() {
        for (Permission permission : Permissions.getPermissions()) {
            log.debug("Loading permission {}", permission.getName());
            savePermissionIfNotExists(permission);
        }
    }

    /**
     * Save the default roles
     */
    private void saveDefaultRoles() {
        for (Role role : Roles.getRoles()) {
            log.debug("Loading role {} with {} permissions", role.getName(), role.getPermissions().size());
            saveRoleIfNotExists(role);
        }
    }

    /**
     * Runs this configuration
     * @param args the arguments to pass to the method
     * @throws Exception if an error occurs
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("Loading default Roles and Permissions into the database if they do not already exist");
        saveDefaultPermissions();
        saveDefaultRoles();
    }
}
