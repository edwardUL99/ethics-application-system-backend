package ie.ul.edward.ethics.users.services;

import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.services.AccountService;
import ie.ul.edward.ethics.users.exceptions.AccountNotExistsException;
import ie.ul.edward.ethics.users.models.User;
import ie.ul.edward.ethics.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation of the UserService interface
 */
@Service
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
     * Construct a UserService with the provided dependencies
     * @param accountService the service for loading accounts
     * @param userRepository the repository for storing users
     */
    @Autowired
    public UserServiceImpl(AccountService accountService, UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    /**
     * Load the user with the given username
     *
     * @param username the username of the user to find
     * @return the user if found, null if not
     */
    @Override
    public User loadUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
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

        userRepository.save(user);

        return user;
    }
}
