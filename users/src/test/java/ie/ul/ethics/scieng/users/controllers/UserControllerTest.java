package ie.ul.ethics.scieng.users.controllers;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.models.*;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.services.UserService;
import ie.ul.ethics.scieng.users.services.UserServiceTest;
import ie.ul.ethics.scieng.users.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.JSON;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.authorization.Roles;
import static ie.ul.ethics.scieng.test.utils.constants.Users.*;

import static ie.ul.ethics.scieng.test.utils.JSON.MEDIA_TYPE;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.USERNAME;
import static ie.ul.ethics.scieng.common.Constants.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is used to test the user controller
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class
}, properties = {
        "auth.jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long",
        "auth.jwt.token.validity=2"
})
public class UserControllerTest {
    /**
     * The bean for authorized users
     */
    @MockBean
    private AuthenticationInformation authenticationInformation;

    /**
     * The mock bean for our user service
     */
    @MockBean
    private UserService userService;

    /**
     * Web app context used for testing
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * Used for mocking interactions with the model view controller
     */
    private MockMvc mockMvc;

    /**
     * Initialises the test mocks
     */
    @BeforeEach
    private void initMocks() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
    }

    /**
     * This test tests that all users should be retrieved
     */
    @Test
    public void shouldGetAllUsers() throws Exception {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = UserServiceTest.createTestUser();
            user.setName("User " + i);
            users.add(user);
        }

        given(userService.getAllUsers())
                .willReturn(users);

        List<UserResponseShortened> response = users.stream()
                .map(UserResponseShortened::new)
                .collect(Collectors.toList());

        String result = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Endpoint.USERS)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).getAllUsers();
    }

    /**
     * This test tests that all users should be retrieved
     */
    @Test
    public void shouldGetAllUsersWithPermission() throws Exception {
        List<User> users = new ArrayList<>();
        List<User> returnedUsers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = UserServiceTest.createTestUser();
            user.setName("User " + i);
            users.add(user);

            if (i >= 3) {
                user.setRole(Roles.COMMITTEE_MEMBER);
                returnedUsers.add(user);
            }
        }

        given(userService.getAllUsers())
                .willReturn(users);

        List<UserResponseShortened> response = returnedUsers.stream()
                .map(UserResponseShortened::new)
                .collect(Collectors.toList());

        String result = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Endpoint.USERS))
                        .param("permission", Permissions.REVIEW_APPLICATIONS.getTag()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).getAllUsers();
    }

    /**
     * This tests that when the request to the user's endpoint is received, it should send the user back
     */
    @Test
    public void shouldLoadUser() throws Exception {
        User user = UserServiceTest.createTestUser();
        UserResponse response = new UserResponse(user);

        String result = JSON.convertJSON(response);

        given(userService.loadUser(USERNAME, false))
                .willReturn(user);

        mockMvc.perform(get(createApiPath(Endpoint.USERS, "user"))
                        .param("username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME, false);
    }

    /**
     * This tests that a user should be loaded successfully by email
     */
    @Test
    public void shouldLoadUserByEmail() throws Exception{
        User user = UserServiceTest.createTestUser();
        UserResponse response = new UserResponse(user);

        String result = JSON.convertJSON(response);

        given(userService.loadUser(USERNAME, true))
                .willReturn(user);

        mockMvc.perform(get(createApiPath(Endpoint.USERS, "user"))
                        .param("username", USERNAME)
                        .param("email", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME, true);
    }

    /**
     * This tests whether 404 not found is returned if the user is not found
     */
    @Test
    public void shouldThrowNotFoundOnLoadUser() throws Exception {
        given(userService.loadUser(USERNAME, false))
                .willReturn(null);

        mockMvc.perform(get(createApiPath(Endpoint.USERS, "user"))
                        .param("username", USERNAME))
                .andExpect(status().isNotFound());

        verify(userService).loadUser(USERNAME, false);
    }

    /**
     * This tests that a user should be created successfully
     */
    @Test
    public void shouldCreateUser() throws Exception {
        User user = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = UserServiceTest.createTestUser();

        given(userService.loadUser(USERNAME))
                .willReturn(null);
        given(userService.createUser(user))
                .willReturn(createdUser);
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        UserResponse userResponse = new UserResponse(createdUser); // the response is the "loaded" user that has been created

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(userResponse);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "user"))
                .contentType(MEDIA_TYPE)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME);
        verify(userService).createUser(user);
        verify(authenticationInformation).getUsername();
    }

    /**
     * This tests that if a user already exists when trying to create a user, a bad request will be thrown
     */
    @Test
    public void shouldThrowBadRequestIfUserExistsOnCreateUser() throws Exception {
        User createdUser = UserServiceTest.createTestUser();

        given(userService.loadUser(USERNAME))
                .willReturn(createdUser);
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, USER_EXISTS);

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that an error is thrown if a user is attempted to be loaded without an account
     */
    @Test
    public void shouldThrowBadRequestIfAccountDoesNotExistOnCreateUser() throws Exception {
        User user = new User(USERNAME, NAME, DEPARTMENT);

        Mockito.doThrow(AccountNotExistsException.class).when(userService).createUser(user);
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ACCOUNT_NOT_EXISTS);

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).createUser(user);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that a 401 Illegal Update should be thrown if a user attempts to create an account for a different user
     * than their authenticated username
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowIllegalUpdateOnCreateOtherUser() throws Exception {
        CreateUpdateUserRequest request = new CreateUpdateUserRequest("not_my_username", NAME, DEPARTMENT);
        String json = JSON.convertJSON(request);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ILLEGAL_UPDATE);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verifyNoInteractions(userService);
        verify(authenticationInformation).getUsername();
    }

    /**
     * This tests that an admin should be able to create any user
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldCreateUserAdmin() throws Exception {
        User user = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = UserServiceTest.createTestUser();

        given(userService.createUser(user))
                .willReturn(createdUser);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        UserResponse userResponse = new UserResponse(createdUser); // the response is the "loaded" user that has been created

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(userResponse);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "admin", "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).createUser(user);
        verifyNoInteractions(authenticationInformation);
    }

    /**
     * This tests that when an admin creates a user that already exists, an error will be thrown
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowErrorIfUserExistsOnCreateUserAdmin() throws Exception {
        User createdUser = UserServiceTest.createTestUser();

        given(userService.loadUser(USERNAME))
                .willReturn(createdUser);
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, USER_EXISTS);

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.USERS, "admin", "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME);
        verifyNoInteractions(authenticationInformation);
    }

    /**
     * This tests that a user should be updated successfully
     * @throws Exception the exception thrown
     */
    @Test
    public void shouldUpdateUser() throws Exception {
        User user = UserServiceTest.createTestUser();

        given(userService.loadUser(USERNAME))
                .willReturn(UserServiceTest.createTestUser());
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        UserResponse userResponse = new UserResponse(user); // the response is the "loaded" user that has been created

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(userResponse);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).updateUser(user);
        verify(authenticationInformation).getUsername();
    }

    /**
     * This tests that a 401 illegal update should be thrown if a user attempts to update another user
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowIllegalUpdateOnUpdateOtherUser() throws Exception {
        CreateUpdateUserRequest request = new CreateUpdateUserRequest("not_my_username", NAME, DEPARTMENT);
        String json = JSON.convertJSON(request);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ILLEGAL_UPDATE);
        String result = JSON.convertJSON(response);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verifyNoInteractions(userService);
        verify(authenticationInformation).getUsername();
    }

    /**
     * This method tests that an admin should be able to update any account
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldUpdateAdmin() throws Exception {
        User user = UserServiceTest.createTestUser();

        given(userService.loadUser(USERNAME))
                .willReturn(user);

        CreateUpdateUserRequest createUpdateUserRequest = new CreateUpdateUserRequest(USERNAME, NAME, DEPARTMENT);
        UserResponse userResponse = new UserResponse(user); // the response is the "loaded" user that has been created

        String json = JSON.convertJSON(createUpdateUserRequest);
        String result = JSON.convertJSON(userResponse);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "admin", "user"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).updateUser(user);
        verifyNoInteractions(authenticationInformation);
    }

    /**
     * This method tests that a user's role should be updated successfully
     */
    @Test
    public void shouldUpdateUserRole() throws Exception {
        User user = UserServiceTest.createTestUser();
        user.setRole(Roles.CHAIR);

        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(USERNAME, Roles.CHAIR.getId());

        String json = JSON.convertJSON(updateRoleRequest);
        String result = JSON.convertJSON(new UserResponse(user));

        given(userService.loadUser(USERNAME))
                .willReturn(user);
        doNothing().when(userService).updateRole(user, Roles.CHAIR);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "user", "role"))
                .contentType(MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME);
        verify(userService).updateRole(user, Roles.CHAIR);
    }

    /**
     * This method tests that if the username does not exist when updating a role, a 404 is thrown
     */
    @Test
    public void shouldThrowNotFoundIfUserNotFoundOnUpdateRole() throws Exception {
        given(userService.loadUser(USERNAME))
                .willReturn(null);

        UpdateRoleRequest request = new UpdateRoleRequest(USERNAME, Roles.CHAIR.getId());
        String json = JSON.convertJSON(request);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "user", "role"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(userService).loadUser(USERNAME);
    }

    /**
     * Tests that if the role is not found from the request, an error is thrown
     */
    @Test
    public void shouldThrowRoleNotFoundOnUpdateRole() throws Exception {
        given(userService.loadUser(USERNAME))
                .willReturn(UserServiceTest.createTestUser());

        UpdateRoleRequest request = new UpdateRoleRequest(USERNAME, 1000000L); // no role should have id 100000
        String json = JSON.convertJSON(request);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ROLE_NOT_FOUND);
        String result = JSON.convertJSON(response);

        mockMvc.perform(put(createApiPath(Endpoint.USERS, "user", "role"))
                        .contentType(MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(userService).loadUser(USERNAME);
    }

    /**
     * This tests that roles should be retrieved successfully
     */
    @Test
    public void shouldGetRoles() throws Exception {
        GetAuthorizationResponse<Role> response = new GetAuthorizationResponse<>(Roles.getRoles());
        String json = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Endpoint.USERS, "roles")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(json));
    }

    /**
     * This tests that roles should be retrieved successfully
     */
    @Test
    public void shouldGetPermissions() throws Exception {
        GetAuthorizationResponse<Permission> response = new GetAuthorizationResponse<>(Permissions.getPermissions());
        String json = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Endpoint.USERS, "permissions")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().json(json));
    }
}
