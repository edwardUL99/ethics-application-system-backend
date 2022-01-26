package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.common.Constants;
import ie.ul.ethics.scieng.users.config.PermissionsAuthorizationConfigurer;
import ie.ul.ethics.scieng.users.config.UserPermissionsConfig;
import ie.ul.ethics.scieng.users.models.AuthorizedUser;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This filter loads the authenticated user from the request and validates that their permissions satisfy any required
 * permissions configured for the request path
 */
@Component
@Log4j2
public class PermissionsAuthorizationFilter extends OncePerRequestFilter {
    /**
     * The information from authentication
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * The authorized user to set for this request
     */
    @Resource(name = "authorizedUser")
    private AuthorizedUser authorizedUser;

    /**
     * This configuration object for user permissions
     */
    private final UserPermissionsConfig userPermissionsConfig;

    /**
     * The service for loading users
     */
    private final UserService userService;

    /**
     * The object for performing user permissions authorization
     */
    private final PermissionsAuthorizer permissionsAuthorizer;

    /**
     * Create the filter with the provided user service
     * @param userService the user service for loading users
     * @param permissionsConfigurer the configuration object used for configuring permissions authorization
     * @param userPermissionsConfig the config object for user permissions
     */
    @Autowired
    public PermissionsAuthorizationFilter(UserService userService, PermissionsAuthorizationConfigurer permissionsConfigurer, UserPermissionsConfig userPermissionsConfig) {
        this.userService = userService;
        this.permissionsAuthorizer = permissionsConfigurer.getAuthorizer();
        this.userPermissionsConfig = userPermissionsConfig;

        if (userPermissionsConfig.isEnabled())
            log.info("Will authorize user permissions for any configured paths");
        else
            log.warn("User permission authorization is disabled. This should be enabled as when disabled, " +
                    "users can perform actions even if not permitted to do so");
    }

    /**
     * Perform the filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = authenticationInformation.getUsername();

        if (userPermissionsConfig.isEnabled() && username != null) {
            User user = userService.loadUser(username);
            String path = request.getRequestURI();
            String method = request.getMethod();

            log.debug("Authorizing permissions for username {}, path {} and method {}", username, path, method);

            if (!permissionsAuthorizer.authorise(path, method, user)) {
                log.error("Failed to authorize permissions for user {} on path {} and method {}", username, path, method);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                PrintWriter writer = response.getWriter();

                writer.print(String.format("{\"%s\": \"%s\"}", Constants.ERROR, Constants.INSUFFICIENT_PERMISSIONS));
                writer.flush();

                return;
            } else {
                authorizedUser.setUser(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}
