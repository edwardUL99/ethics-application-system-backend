package ie.ul.edward.ethics.users.config;

import ie.ul.edward.ethics.users.models.AuthorizedUser;
import ie.ul.edward.ethics.users.repositories.PermissionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This class creates beans within the users module
 */
@Configuration
public class UserConfiguration {
    /**
     * The permission repository used for loading and saving permissions
     */
    private final PermissionRepository permissionRepository;

    /**
     * Create a UserConfiguration object
     * @param permissionRepository the permission repository used for loading and saving permissions
     */
    public UserConfiguration(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Create the bean for configuring the user role authorization
     * @return the PermissionsAuthorizationConfigurer object for configuration
     */
    @Bean
    public PermissionsAuthorizationConfigurer permissionsConfigurer() {
        return new PermissionsAuthorizationConfigurer(permissionRepository);
    }

    /**
     * Create a request scoped authorized user bean
     * @return the request scoped bean
     */
    @Bean
    @RequestScope
    public AuthorizedUser authorizedUser() {
        return new AuthorizedUser();
    }
}
