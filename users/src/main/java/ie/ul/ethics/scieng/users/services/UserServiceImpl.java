package ie.ul.ethics.scieng.users.services;

import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.services.AccountService;
import ie.ul.ethics.scieng.users.config.UserPermissionsConfig;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.repositories.UserRepository;
import ie.ul.ethics.scieng.users.authorization.Roles;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides the implementation of the UserService interface
 */
@Service
@Log4j2
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {
    /**
     * The account service for retrieving accounts
     */
    private final AccountService accountService;

    /**
     * The repository for storing users
     */
    private final UserRepository userRepository;

    /**
     * The configuration for user permissions
     */
    private final UserPermissionsConfig userPermissionsConfig;

    /**
     * Construct a UserService with the provided dependencies
     * @param accountService the service for loading accounts
     * @param userRepository the repository for storing users
     * @param userPermissionsConfig the configuration for user permissions
     */
    @Autowired
    public UserServiceImpl(AccountService accountService, UserRepository userRepository, UserPermissionsConfig userPermissionsConfig) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.userPermissionsConfig = userPermissionsConfig;
    }

    /**
     * Retrieve all users in the system
     *
     * @return the list of users
     */
    @Override
    @Cacheable(value = "allusers")
    public List<User> getAllUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        List<User> users = new ArrayList<>();
        allUsers.forEach(users::add);

        return users;
    }

    /**
     * Load the user with the given username
     *
     * @param username the username of the user to find
     * @return the user if found, null if not
     */
    @Override
    @Cacheable(value = "user")
    public User loadUser(String username) {
        return loadUser(username, false);
    }

    /**
     * Load the user with the given username
     *
     * @param username the username to load the user with
     * @param email    true if username is to be treated as email, false if username
     * @return the user if found, null if not
     */
    @Override
    @Cacheable(value = "user")
    public User loadUser(String username, boolean email) {
        Optional<User> optional = (email) ? userRepository.findByAccount_Email(username):userRepository.findByUsername(username);
        return optional.orElse(null);
    }

    /**
     * Check if the user's email address matches the specified chair email and if so, assign the chair role if so
     * @param user the user to check
     */
    private void checkUserRole(User user) {
        String email = user.getAccount().getEmail();

        Role role = Roles.APPLICANT;

        if (email.equals(userPermissionsConfig.getChair())) {
            List<User> currentChairs = userRepository.findByRole_Name(Roles.CHAIR.getName());

            if (currentChairs.size() == 0 || !Roles.CHAIR.isSingleUser()) {
                log.info("User signed up with Chair email {}. Assigning Chair role", email);
                role = Roles.CHAIR;
            }
        }

        user.setRole(role);
    }

    /**
     * Create a new user by loading the user's account and save it
     *
     * @param user the user to create. Should be constructed using the {@link User#User(String, String, String)} constructor
     * @return the created user with loaded account
     * @throws AccountNotExistsException if no account exists for the user's username
     * @throws IllegalStateException     if the account is not created using the {@link User#User(String, String, String)} constructor
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "allusers", allEntries = true)
    })
    public User createUser(User user) {
        String username = user.getUsername();
        Account account = user.getAccount();

        if (account != null)
            throw new IllegalStateException("The user passed to this method should be constructed using the User(String, String, String) constructor" +
                    " and have no account set, i.e. user.getAccount() should return null");

        account = accountService.getAccount(username);

        if (account == null)
            throw new AccountNotExistsException(username);

        user.setAccount(account);
        checkUserRole(user);

        return userRepository.save(user);
    }

    /**
     * This method updates the user
     *
     * @param user the user the update
     * @throws IllegalStateException if the user's account has changed as the account must stay the same and not be
     * updated in this request
     * @throws AccountNotExistsException if there is no saved account for this user
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "allusers", allEntries = true)
    })
    public void updateUser(User user) {
        String username = user.getUsername();

        Account account = user.getAccount();
        Account savedAccount = accountService.getAccount(username);

        if (savedAccount == null)
            throw new AccountNotExistsException(username);
        else if (!account.equals(savedAccount)) {
            user.setAccount(savedAccount);
        }

        userRepository.save(user);
    }

    /**
     * Downgrade any users with the specified role to the role specified by downgrade
     * @param role the role to match users by
     *
     */
    private void downgradeRoles(Role role) {
        String name = role.getName();
        Role downgrade = Roles.getRole(role.getDowngradeTo());
        String downgradeName = downgrade.getName();
        List<User> users = userRepository.findByRole_Name(name);

        for (User u : users) {
            log.info("Can only have one user with role {}, so downgrading user {} to role {}", name, u.getUsername(), downgradeName);
            u.setRole(downgrade);
            userRepository.save(u);
        }
    }

    /**
     * This method updates the user's role. If the role is chair and a chair already exists, the existing chair is demoted
     * to a committee member
     *
     * @param user the user to update
     * @param role the role to change
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "allusers", allEntries = true)
    })
    public void updateRole(User user, Role role) {
        if (role.isSingleUser())
            downgradeRoles(role);

        user.setRole(role);

        userRepository.save(user);
    }

    /**
     * Search for users with the given specification
     *
     * @param specification the specification to search with
     * @return the list of found users
     */
    @Override
    public List<User> search(Specification<User> specification) {
        return this.userRepository.findAll(specification);
    }
}
