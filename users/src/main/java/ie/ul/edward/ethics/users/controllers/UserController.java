package ie.ul.edward.ethics.users.controllers;

import ie.ul.edward.ethics.authentication.jwt.AuthenticationInformation;
import ie.ul.edward.ethics.users.exceptions.AccountNotExistsException;
import ie.ul.edward.ethics.users.models.CreateUpdateUserRequest;
import ie.ul.edward.ethics.users.models.UserResponse;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.services.UserService;
import static ie.ul.edward.ethics.common.Constants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller is used for providing endpoints for users
 */
@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    /**
     * The user service for interacting with user business logic
     */
    private final UserService userService;

    /**
     * The authentication information for the request
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * Create the user controller with the provided user service
     * @param userService the user service for interacting with user business logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * This endpoint loads the user with the provided username
     * @param username the username of the user to load
     * @return the response body
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String username) {
        User user = userService.loadUser(username);

        if (user == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(new UserResponse(user));
    }

    /**
     * The internal method to create a user. Assumes necessary authentication is already performed
     * @param request the request to create a user
     * @param update true if updating, false if not
     * @return the response body
     */
    private ResponseEntity<?> createUserInternal(CreateUpdateUserRequest request, boolean update) {
        try {
            User user = new User(request.getUsername(), request.getName(), request.getDepartment());
            user = userService.createUser(user);

            return ResponseEntity.status((update) ? HttpStatus.OK:HttpStatus.CREATED).body(new UserResponse(user));
        } catch (AccountNotExistsException ex) {
            Map<String, Object> response = new HashMap<>();

            response.put(ERROR, ACCOUNT_NOT_EXISTS);

            return ResponseEntity.badRequest().body(response);
        }
    }

    /*
     * TODO determine if update should be separate to createUser or if createUser is sufficient to also perform an update
     */

    /**
     * This endpoint creates/updates a user. The username must match the authenticated username
     * @param request the request to create the user with
     * @return the response body
     */
    @RequestMapping(value = "/user", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUpdateUserRequest request, HttpServletRequest servletRequest) {
        boolean update = servletRequest.getMethod().equalsIgnoreCase("PUT");

        String authenticatedUsername = authenticationInformation.getUsername();

        if (authenticatedUsername == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (!authenticatedUsername.equals(request.getUsername())) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERROR, ILLEGAL_UPDATE);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            return createUserInternal(request, update);
        }
    }

    /**
     * The endpoint for an admin to create/update any account without username verification.
     * This endpoint should be locked to those users with ADMIN permissions
     * @param request the request to create/update the user
     * @return the response body
     */
    @RequestMapping(value = "/admin/user", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> createUserAdmin(@RequestBody @Valid CreateUpdateUserRequest request, HttpServletRequest servletRequest) {
        return createUserInternal(request, servletRequest.getMethod().equalsIgnoreCase("PUT"));
    }

    // TODO see OneNote FYP TODOs notebook for security hole in this createUser method and also add more tests and endpoints for updating permissions (these endpoints should be locked by permissions validation)
}
