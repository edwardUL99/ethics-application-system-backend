package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.models.ApplicationResponseFactory;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import static ie.ul.ethics.scieng.common.Constants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.function.Function;

/**
 * The default implementation for the response service
 */
@Service
public class ApplicationResponseServiceImpl implements ApplicationResponseService {
    /**
     * User service to load authenticated users
     */
    private final UserService userService;
    /**
     * Authentication information to retrieve user's username
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * Create an instance
     * @param userService the service to create
     */
    public ApplicationResponseServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads the authenticated user
     * @return the loaded user
     */
    private User loadUser() {
        if (this.authenticationInformation != null) {
            String username = this.authenticationInformation.getUsername();

            if (username != null)
                return userService.loadUser(username);
        }

        return null;
    }

    /**
     * Perform the task and return the response
     *
     * @param task  the task to perform
     * @return the response body
     */
    @Override
    public ResponseEntity<?> process(Function<Void, Application> task) {
        return process(task, HttpStatus.OK);
    }

    /**
     * Perform the task and return the response
     *
     * @param task   the task to perform
     * @param status a different status to specify
     * @return the response body
     */
    @Override
    public ResponseEntity<?> process(Function<Void, Application> task, HttpStatus status) {
        User user = loadUser();

        if (user == null) {
            return respondError(INSUFFICIENT_PERMISSIONS);
        } else {
            try {
                Application response = task.apply(null);

                if (response == null)
                    return ResponseEntity.notFound().build();

                response = response.clean(user);

                return ResponseEntity.status(status).body(ApplicationResponseFactory.buildResponse(response));
            } catch (TaskInterrupt interrupt) {
                return interrupt.getResponse();
            }
        }
    }
}
