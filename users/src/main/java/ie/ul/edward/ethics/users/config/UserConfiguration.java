package ie.ul.edward.ethics.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This class creates beans within the users module
 */
@Configuration
public class UserConfiguration {
    /**
     * Creates a request scoped UserInformation bean
     * @return the created bean
     */
    @Bean
    @RequestScope
    public UserInformation userInformation() {
        return new UserInformation();
    }
}
