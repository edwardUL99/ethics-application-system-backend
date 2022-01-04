package ie.ul.edward.ethics.users.services;

import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.services.AccountService;
import ie.ul.edward.ethics.users.exceptions.AccountNotExistsException;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.repositories.UserRepository;
import ie.ul.edward.ethics.users.authorization.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;

import static ie.ul.edward.ethics.test.utils.constants.Authentication.*;
import static ie.ul.edward.ethics.test.utils.constants.Users.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * This test tests the user service
 */
@SpringBootTest(classes = {
        ie.ul.edward.ethics.test.utils.TestApplication.class,
        ie.ul.edward.ethics.users.test.config.TestConfiguration.class,
        ie.ul.edward.ethics.authentication.jwt.JWT.class,
        ie.ul.edward.ethics.authentication.jwt.JwtRequestFilter.class
}, properties = {
        "auth.jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long",
        "auth.jwt.token.validity=2",
        "permissions.authorization.enabled=true",
        CHAIR_EMAIL_PROPERTY,
        ADMINISTRATOR_EMAIL_PROPERTY
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
        return new Account(USERNAME, EMAIL, PASSWORD);
    }

    /**
     * Creates a test user
     * @return the test user
     */
    public static User createTestUser() {
        return new User(NAME, createTestAccount(), DEPARTMENT, Roles.STANDARD_USER);
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

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(createdUser.getRole(), Roles.STANDARD_USER);
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

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(returned.getRole(), Roles.CHAIR);
        verify(accountService).getAccount(USERNAME);
        verify(userRepository).findByRole_Name(Roles.CHAIR.getName());
        verify(userRepository).save(newUser);
    }

    /**
     * Tests that chairperson role is not set if a chair already is set-up in the system
     */
    @Test
    public void shouldNotSetChairRoleIfChairAlreadyExists() {
        User newUser = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = createTestUser();
        createdUser.getAccount().setEmail(CHAIR_EMAIL);

        given(accountService.getAccount(USERNAME))
                .willReturn(createdUser.getAccount());
        given(userRepository.findByRole_Name(Roles.CHAIR.getName()))
                .willReturn(Collections.singletonList(createdUser));

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(returned.getRole(), Roles.STANDARD_USER);
        verify(accountService).getAccount(USERNAME);
        verify(userRepository).findByRole_Name(Roles.CHAIR.getName());
        verify(userRepository).save(newUser);
    }

    /**
     * This test tests that the user is assigned administrator role if the email matches the administrator email
     */
    @Test
    public void shouldSetAdministratorRoleOnCreateUser() {
        User newUser = new User(USERNAME, NAME, DEPARTMENT);
        User createdUser = createTestUser();
        createdUser.setRole(Roles.ADMINISTRATOR);
        createdUser.getAccount().setEmail(ADMINISTRATOR_EMAIL);

        given(accountService.getAccount(USERNAME))
                .willReturn(createdUser.getAccount());

        User returned = userService.createUser(newUser);

        assertEquals(createdUser, returned);
        assertEquals(returned.getRole(), Roles.ADMINISTRATOR);
        verify(accountService).getAccount(USERNAME);
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
}
