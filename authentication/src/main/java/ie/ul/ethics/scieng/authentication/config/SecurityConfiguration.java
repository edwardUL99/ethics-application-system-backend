package ie.ul.ethics.scieng.authentication.config;

import ie.ul.ethics.scieng.authentication.jwt.JwtAuthenticationEntrypoint;
import ie.ul.ethics.scieng.authentication.jwt.JwtRequestFilter;
import ie.ul.ethics.scieng.common.properties.PropertyFinder;
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
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

import static ie.ul.ethics.scieng.common.Constants.*;

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
        String frontendURL = PropertyFinder.findProperty("ETHICS_FRONTEND_URL", "frontend.url");
        frontendURL = (frontendURL == null) ? "http://localhost:4200":frontendURL;

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList(frontendURL));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setExposedHeaders(Collections.singletonList("*"));
        List<String> allowedMethods= List.of("GET", "POST", "PUT", "HEAD", "PATCH", "DELETE");
        corsConfiguration.setAllowedMethods(allowedMethods);

        http.csrf().disable()
                .authorizeRequests().antMatchers(
                        createApiPath(Endpoint.AUTHENTICATION, "register"),
                        createApiPath(Endpoint.AUTHENTICATION, "login"),
                        createApiPath(Endpoint.AUTHENTICATION, true,"account", "confirmed"),
                        createApiPath(Endpoint.AUTHENTICATION, "account", "confirm"),
                        createApiPath(Endpoint.AUTHENTICATION, true, "account", "confirm", "resend"),
                        createApiPath(Endpoint.AUTHENTICATION, true, "forgot-password"),
                        createApiPath(Endpoint.AUTHENTICATION, "reset-password"),
                        createApiPath(Endpoint.EXPORT, true, "download")
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .cors().configurationSource(request -> corsConfiguration)
                .and()
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
