package ie.ul.edward.ethics.users.controllers;

import ie.ul.edward.ethics.users.exceptions.AccountNotExistsException;
import ie.ul.edward.ethics.users.models.CreateUserRequest;
import ie.ul.edward.ethics.users.models.UserResponse;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.services.UserService;
import static ie.ul.edward.ethics.common.Constants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * This endpoint creates a user
     * @param request the request to create the user with
     * @return the response body
     */
    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        try {
            User user = new User(request.getUsername(), request.getName(), request.getDepartment());
            user = userService.createUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
        } catch (AccountNotExistsException ex) {
            Map<String, Object> response = new HashMap<>();

            response.put(ERROR, ACCOUNT_NOT_EXISTS);

            return ResponseEntity.badRequest().body(response);
        }
    }

    // TODO see OneNote FYP TODOs notebook for security hole in this createUser method and also add more tests and endpoints for updating permissions (these endpoints should be locked by permissions validation)
}
