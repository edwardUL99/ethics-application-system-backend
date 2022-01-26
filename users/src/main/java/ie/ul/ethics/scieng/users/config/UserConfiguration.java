package ie.ul.ethics.scieng.users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.ul.ethics.scieng.users.models.AuthorizedUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This class creates beans within the users module
 */
@Configuration
@Log4j2
public class UserConfiguration {
    /**
     * Create the bean for configuring the user role authorization
     * @return the PermissionsAuthorizationConfigurer object for configuration
     */
    @Bean
    public PermissionsAuthorizationConfigurer permissionsConfigurer() {
        return new PermissionsAuthorizationConfigurer();
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

    /**
     * Read in the permissions paths config object if defined
     * @return the configuration for paths
     */
    @Bean
    public PermissionsPathsConfig permissionsPathsConfig() throws Exception {
        Resource resource = new ClassPathResource("permissions.json");

        if (resource.exists()) {
            log.info("Picking up permissions paths configuration from {}", resource.getURL());
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(resource.getInputStream(), PermissionsPathsConfig.class);
        } else {
            log.info("No permissions.json file found on the classpath, reverting to default permissions paths configuration");
            return new PermissionsPathsConfig();
        }
    }
}
