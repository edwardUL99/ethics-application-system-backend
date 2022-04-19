package ie.ul.ethics.scieng.users.services;

import ie.ul.ethics.scieng.authentication.jwt.JWT;
import ie.ul.ethics.scieng.authentication.jwt.JwtRequestFilter;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.services.AccountService;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.repositories.UserRepository;
import ie.ul.ethics.scieng.users.test.config.TestConfiguration;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.authorization.Roles;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;
import static ie.ul.ethics.scieng.test.utils.constants.Users.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * This test tests the user service
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class,
        JWT.class,
        JwtRequestFilter.class
}, properties = {
        "auth.jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long",
        "auth.jwt.token.validity=2",
        "permissions.authorization.enabled=true",
        CHAIR_EMAIL_PROPERTY
})
public class UserServiceTest {
    /**
     * The account service mock bean
     */
    @MockBean
    private AccountService accountService;
    /**
     * The user repository mock bean
     */
    @MockBean
    private UserRepository userRepository;
    /**
     * The user service being tested
     */
    @Autowired
    private UserService userService;

    /**
     * Creates a test account
     * @return the test account
     */
    public static Account createTestAccount() {
        return new Account(USERNAME, EMAIL, PASSWORD, false);
    }

    /**
     * Creates a test user
     * @return the test user
     */
    public static User createTestUser() {
        return new User(NAME, createTestAccount(), DEPARTMENT, Roles.APPLICANT);
    }

    /**
     * Tests that all users should be retrieved successfully
     */
    @Test
    public void shouldGetAllUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = createTestUser();
            user.setName("User " + i);
            users.add(user);
        }

        given(userRepository.findAll())
                .willReturn(users);

        List<User> found = userService.getAllUsers();

        assertEquals(users, found);
        verify(userRepository).findAll();
    }

    /**
     * This method tests that a user should be loaded successfully
     */
    @Test
    public void shouldLoadUserSuccessfully() {
        User user = createTestUser();

        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));

        User returned = userService.loadUser(USERNAME);

        assertEquals(user, returned);
        verify(userRepository).findByUsername(USERNAME);
    }

    /**
     * This method tests that a user should be loaded by email successfully
     */
    @Test
    public void shouldLoadUserByEmail() {
        User user = createTestUser();

        given(userRepository.findByAccount_Email(EMAIL))
                .willReturn(Optional.of(user));

        User returned = userService.loadUser(EMAIL, true);

        assertEquals(user, returned);
        verify(userRepository).findByAccount_Email(EMAIL);
    }

    /**
     * This method tests that null should be returned if the username does not exist
     */
    @Test
    public void shouldReturnNullOnLoadUserIfNotfound() {
        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        User returned = userService.loadUser(USERNAME);

        assertNull(returned);
    }

    /**
     * This method tests that a user should be created successfully by loading the account and saving
     */
    @Test
    public void shouldCreateUserSuccessfully() {
        User newUser = new User(USERNAME, NAME, DEPARTMENT); // the user we'll create
        User createdUser = createTestUser();
        Account account = createdUser.getAccount();

        given(accountService.getAccount(USERNAME))
                .willReturn(account);
        given(userRepository.save(createdUser))
                .willReturn(createdUser);

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(createdUser.getRole(), Roles.APPLICANT);
        verify(accountService).getAccount(USERNAME);
        verify(userRepository).save(newUser);
    }

    /**
     * This test tests that the user is assigned chair role if the email matches the chair email
     */
    @Test
    public void shouldSetChairRoleOnCreateUser() {
        User newUser = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = createTestUser();
        createdUser.setRole(Roles.CHAIR);
        createdUser.getAccount().setEmail(CHAIR_EMAIL);

        given(accountService.getAccount(USERNAME))
                .willReturn(createdUser.getAccount());
        given(userRepository.findByRole_Name(Roles.CHAIR.getName()))
                .willReturn(Collections.emptyList());
        given(userRepository.save(createdUser))
                .willReturn(createdUser);

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(returned.getRole(), Roles.CHAIR);
        verify(accountService).getAccount(USERNAME);
        verify(userRepository).findByRole_Name(Roles.CHAIR.getName());
        verify(userRepository).save(newUser);
    }

    /**
     * Tests that chairperson role is not set if a chair already is set up in the system
     */
    @Test
    public void shouldNotSetChairRoleIfChairAlreadyExists() {
        Role role = Roles.CHAIR;
        String name = role.getName();

        User newUser = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = createTestUser();
        createdUser.getAccount().setEmail(CHAIR_EMAIL);

        given(accountService.getAccount(USERNAME))
                .willReturn(createdUser.getAccount());
        given(userRepository.findByRole_Name(name))
                .willReturn(Collections.singletonList(createdUser));
        given(userRepository.save(createdUser))
                .willReturn(createdUser);

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(returned.getRole(), Roles.APPLICANT);
        verify(accountService).getAccount(USERNAME);
        verify(userRepository).findByRole_Name(name);
        verify(userRepository).save(newUser);
    }

    /**
     * This method tests that IllegalStateException should be thrown if the account of the user being created is not null
     */
    @Test
    public void shouldThrowIllegalStateOnCreateUser() {
        User user = createTestUser();

        assertThrows(IllegalStateException.class, () -> userService.createUser(user));

        verifyNoInteractions(accountService);
    }

    /**
     * This method tests that AccountNotExistsException is thrown if no account is created
     */
    @Test
    public void shouldThrowAccountNotExists() {
        User user = new User(USERNAME, NAME, DEPARTMENT);

        given(accountService.getAccount(USERNAME))
                .willReturn(null);

        assertThrows(AccountNotExistsException.class, () -> userService.createUser(user));

        verify(accountService).getAccount(USERNAME);
    }

    /**
     * This method tests that the user should be updated
     */
    @Test
    public void shouldUpdateUser() {
       User user = createTestUser();

       given(accountService.getAccount(USERNAME))
               .willReturn(user.getAccount());

       userService.updateUser(user);

       verify(accountService).getAccount(USERNAME);
       verify(userRepository).save(user);
    }

    /**
     * This method tests that if a user is attempted to be updated with no account associated,
     * an AccountNotExists exception is thrown
     */
    @Test
    public void shouldThrowAccountNotExistsOnUpdate() {
        User user = createTestUser();

        given(accountService.getAccount(USERNAME))
                .willReturn(null);

        assertThrows(AccountNotExistsException.class, () -> userService.updateUser(user));

        verify(accountService).getAccount(USERNAME);
        verify(userRepository, times(0)).save(user);
    }

    /**
     * This method tests the upgrade of a role from standard member to committee member
     */
    @Test
    public void shouldUpdateNormalRole() {
        User user = createTestUser();

        userService.updateRole(user, Roles.COMMITTEE_MEMBER);

        assertEquals(user.getRole(), Roles.COMMITTEE_MEMBER);
        verify(userRepository).save(user);
    }

    /**
     * Performs testing where updating to the role requires existing users with that role to be downgraded to committee members
     * @param role the role being updated to
     */
    private void testUpdateRoleDowngrade(Role role) {
        String name = role.getName();

        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            User user = createTestUser();
            user.setRole(Roles.CHAIR);
            userList.add(user);
        }

        User user = createTestUser();

        given(userRepository.findByRole_Name(name))
                .willReturn(userList);

        userService.updateRole(user, role);

        Assertions.assertEquals(user.getRole(), role);

        for (User u : userList) {
            assertEquals(u.getRole(), Roles.COMMITTEE_MEMBER);
            verify(userRepository, times(userList.size())).save(u);
        }

        verify(userRepository).findByRole_Name(name);
        verify(userRepository).save(user);
    }

    /**
     * This method tests that if the requested role to update to is chair and that any existing chairs are downgraded
     * to committee members
     */
    @Test
    public void shouldUpdateRoleToChair() {
        testUpdateRoleDowngrade(Roles.CHAIR);
    }
}
