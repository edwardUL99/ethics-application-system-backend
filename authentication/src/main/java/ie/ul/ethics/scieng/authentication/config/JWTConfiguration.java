package ie.ul.ethics.scieng.authentication.config;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This class provides configuration for JWT
 */
@Configuration
public class JWTConfiguration {
    /**
     * Creates a request scoped bean for setting auth information
     * @return an AuthenticationInformation instance
     */
    @Bean
    @RequestScope
    public AuthenticationInformation authenticationInformation() {
        return new AuthenticationInformation();
    }
}
