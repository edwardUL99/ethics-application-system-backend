package ie.ul.edward.ethics.users.filters;

import ie.ul.edward.ethics.authentication.jwt.AuthenticationInformation;
import ie.ul.edward.ethics.users.config.UserInformation;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.services.UserService;
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

/**
 * This filter loads the authenticated user from the request
 */
@Component
@Log4j2
public class LoadUserFilter extends OncePerRequestFilter {
    /**
     * The information from authentication
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;
    /**
     * The user information to load the authenticated user on
     */
    @Resource(name = "userInformation")
    private UserInformation userInformation;

    /**
     * The service for loading users
     */
    private final UserService userService;

    /**
     * Create the filter with the provided user service
     * @param userService the user service for loading users
     */
    @Autowired
    public LoadUserFilter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Perform the filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = authenticationInformation.getUsername();

        if (username != null) {
            User user = userService.loadUser(username);

            if (user != null) {
                log.info("Setting authenticated user for request with username {}", username);
                userInformation.setUser(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}
