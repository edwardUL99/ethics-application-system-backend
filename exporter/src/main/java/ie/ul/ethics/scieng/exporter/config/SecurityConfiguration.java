package ie.ul.ethics.scieng.exporter.config;

import static ie.ul.ethics.scieng.common.Constants.Endpoint;
import static ie.ul.ethics.scieng.common.Constants.createApiPath;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security Configuration to expose exporter download links
 */
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * Configure the http security for the endpoints
     * @param http the security config object
     * @throws Exception if an error occurs configuring it
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(
                        createApiPath(Endpoint.EXPORT, true, "download")
                )
                .permitAll();
    }
}
