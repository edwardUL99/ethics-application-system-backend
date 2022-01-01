package ie.ul.edward.ethics.users.roles;

import ie.ul.edward.ethics.authentication.jwt.AuthenticationInformation;
import ie.ul.edward.ethics.users.config.PermissionsConfigurer;
import ie.ul.edward.ethics.users.models.AuthorizedUser;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class UserAuthorizationFilter extends OncePerRequestFilter {
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
     * This config property enables/disabled the permissions authorization
     */
    @Value("${permissions.authorization.enabled}")
    private boolean enabled;

    /**
     * The service for loading users
     */
    private final UserService userService;

    /**
     * The object for performing user permissions authorization
     */
    private final UserAuthorizer userAuthorizer;

    /**
     * Create the filter with the provided user service
     * @param userService the user service for loading users
     * @param permissionsConfigurer the configuration object used for configuring permissions authorization
     */
    @Autowired
    public UserAuthorizationFilter(UserService userService, PermissionsConfigurer permissionsConfigurer) {
        this.userService = userService;
        this.userAuthorizer = permissionsConfigurer.getAuthorizer();
    }

    /**
     * Perform the filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = authenticationInformation.getUsername();

        if (enabled && username != null) {
            User user = userService.loadUser(username);
            String path = request.getRequestURI();

            if (!userAuthorizer.authorise(path, user)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                PrintWriter writer = response.getWriter();

                writer.print("{\"error\": \"insufficient_permissions\"}"); // todo make a constant
                writer.flush();

                return;
            } else {
                authorizedUser.setUser(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}
