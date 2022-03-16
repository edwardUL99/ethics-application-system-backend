package ie.ul.ethics.scieng.users.controllers;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.common.search.SearchController;
import ie.ul.ethics.scieng.common.search.SearchException;
import ie.ul.ethics.scieng.common.search.SearchParser;
import ie.ul.ethics.scieng.common.search.SearchResponse;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.authorization.Roles;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.models.*;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.search.UserSpecification;
import ie.ul.ethics.scieng.users.services.UserService;
import static ie.ul.ethics.scieng.common.Constants.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This controller is used for providing endpoints for users
 */
@RestController
@RequestMapping("/api/users")
public class UserController implements SearchController<UserResponseShortened> {
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
     * This endpoint retrieves all the users in the system or users with a given permission
     * @param permission return the list of users with the given permission. Expected to be the tag name
     * @return the response body
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String permission) {
        Permission mappedPermission = (permission != null) ? Permissions.getPermissionByFieldName(permission):null;

        return ResponseEntity.ok(userService.getAllUsers()
                .stream()
                .filter(user -> mappedPermission == null || user.getRole().getPermissions().contains(mappedPermission))
                .map(UserResponseShortened::new)
                .collect(Collectors.toList()));
    }

    /**
     * This endpoint loads the user with the provided username
     * @param username the username of the user to load
     * @param email, true if the username if an email, false if just username
     * @return the response body
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String username, @RequestParam(required = false) boolean email) {
        User user = userService.loadUser(username, email);

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
            User user = userService.loadUser(request.getUsername());

            if (update) {
                if (user == null)
                    return ResponseEntity.notFound().build();

                user.setName(request.getName());
                user.setDepartment(request.getDepartment());

                userService.updateUser(user);

                return ResponseEntity.ok(new UserResponse(user));
            } else {
                if (user != null) {
                    return respondError(USER_EXISTS);
                }

                user = new User(request.getUsername(), request.getName(), request.getDepartment());
                user = userService.createUser(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
            }
        } catch (AccountNotExistsException ex) {
            return respondError(ACCOUNT_NOT_EXISTS);
        }
    }

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

    /**
     * The endpoint for updating user roles. This should be locked behind a grant permissions permission
     * @param request the request for updating the user's role
     * @return the response body
     */
    @PutMapping("/user/role")
    public ResponseEntity<?> updateUserRole(@RequestBody @Valid UpdateRoleRequest request) {
        User user = userService.loadUser(request.getUsername());

        if (user == null)
            return ResponseEntity.notFound().build();

        Role role = Roles.getRoles().stream()
                .filter(r -> r.getId().equals(request.getRole()))
                .findFirst().orElse(null);

        if (role == null) {
            return respondError(ROLE_NOT_FOUND);
        } else {
            userService.updateRole(user, role);

            return ResponseEntity.ok(new UserResponse(user));
        }
    }

    /**
     * This endpoint returns a listing of all roles in the system
     * @return the response body
     */
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(new GetAuthorizationResponse<>(Roles.getRoles()));
    }

    /**
     * This endpoint returns a listing of all permissions in the system
     * @return the response body
     */
    @GetMapping("/permissions")
    public ResponseEntity<?> getPermissions() {
        return ResponseEntity.ok(new GetAuthorizationResponse<>(Permissions.getPermissions()));
    }

    /**
     * This endpoint allows searching for users
     * @param query the search string
     * @param or true to or multiple specifications, otherwise default is and
     * @return the response body
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse<UserResponseShortened>> search(@RequestParam String query, @RequestParam(required = false) boolean or) {
        Specification<User> specification = new SearchParser<>(UserSpecification.class)
                .parse(query, UserSpecification.OPERATION_PATTERN, or);

        if (specification == null) {
            return ResponseEntity.badRequest().body(new SearchResponse<>(List.of(), SEARCH_FAILED));
        } else {
            try {
                List<UserResponseShortened> shortened = this.userService.search(specification)
                        .stream()
                        .map(UserResponseShortened::new)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new SearchResponse<>(shortened, null));
            } catch (SearchException ex) {
                ex.printStackTrace();
                return ResponseEntity.badRequest().body(new SearchResponse<>(List.of(), SEARCH_FAILED));
            }
        }
    }
}
