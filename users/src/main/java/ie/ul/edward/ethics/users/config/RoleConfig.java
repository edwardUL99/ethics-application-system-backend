package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.models.roles.Permission;
import ie.ul.edward.ethics.users.models.roles.Role;
import ie.ul.edward.ethics.users.repositories.PermissionRepository;
import ie.ul.edward.ethics.users.repositories.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     */
    @Autowired
    public RoleConfig(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Save the permission if it doesn't exist
     * @param permission the permission to save
     */
    private void savePermissionIfNotExists(Permission permission) {
        Optional<Permission> foundPermission = permissionRepository.findByName(permission.getName());

        if (foundPermission.isEmpty())
            permissionRepository.save(permission);
        else
            permission.setId(foundPermission.get().getId());
    }

    /**
     * Save the role if not exists
     * @param role the role to save
     */
    private void saveRoleIfNotExists(Role role) {
        Optional<Role> foundRole = roleRepository.findByName(role.getName());

        if (foundRole.isEmpty())
            roleRepository.save(role);
        else
            role.setId(foundRole.get().getId());
    }

    /**
     * Save the default permissions
     */
    private void saveDefaultPermissions() throws Exception {
        Permission permissionObj = new Permission();

        List<Field> fields = Arrays.stream(Permission.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toList());

        for (Field field : fields) {
            Permission permission = (Permission)field.get(permissionObj);
            log.debug("Loading permission {}", permission.getName());
            savePermissionIfNotExists(permission);
        }
    }

    /**
     * Save the default roles
     */
    private void saveDefaultRoles() throws Exception {
        Role roleObj = new Role();

        List<Field> fields = Arrays.stream(Role.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) &&
                        Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toList());

        for (Field field : fields) {
            Role role = (Role)field.get(roleObj);
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
