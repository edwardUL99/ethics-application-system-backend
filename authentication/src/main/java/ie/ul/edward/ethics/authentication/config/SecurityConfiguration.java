package ie.ul.edward.ethics.authentication.config;

import ie.ul.edward.ethics.authentication.jwt.JwtAuthenticationEntrypoint;
import ie.ul.edward.ethics.authentication.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static ie.ul.edward.ethics.common.Constants.*;

/**
 * This class is responsible for setting up the authentication of the back-end and the different endpoints it provides
 */
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * The entrypoint for jwt authentication
     */
    private final JwtAuthenticationEntrypoint entrypoint;
    /**
     * The service for retrieving user details
     */
    private final UserDetailsService userDetailsService;
    /**
     * The request filter for JWT processing
     */
    private final JwtRequestFilter requestFilter;

    /**
     * Create a SecurityConfiguration
     * @param entrypoint the entrypoint for jwt authentication
     * @param userDetailsService the service for retrieving user details
     * @param requestFilter the request filter for jwt processing
     */
    @Autowired
    public SecurityConfiguration(JwtAuthenticationEntrypoint entrypoint, UserDetailsService userDetailsService, JwtRequestFilter requestFilter) {
        this.entrypoint = entrypoint;
        this.userDetailsService = userDetailsService;
        this.requestFilter = requestFilter;
    }

    /**
     * Configures the authentication manager globally
     * @param auth the auth builder
     * @throws Exception if configuration fails
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Expose the authentication manager as a bean
     * @return auth manager
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configure the http security for the endpoints
     * @param http the security config object
     * @throws Exception if an error occurs configuring it
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(
                        createApiPath(Endpoint.AUTHENTICATION, "*")
                ).permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(entrypoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * The encoder used for encoding passwords
     * @return the password encoder implementation for the system
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
