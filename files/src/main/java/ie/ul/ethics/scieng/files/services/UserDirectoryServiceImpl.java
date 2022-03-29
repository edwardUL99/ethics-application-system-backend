package ie.ul.ethics.scieng.files.services;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class represents the implementation of the UserDirectoryService
 */
@Service
public class UserDirectoryServiceImpl implements UserDirectoryService {
    /**
     * The service for loading users
     */
    private final UserService userService;
    /**
     * The information of the authenticated user
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * Create an UserDirectoryServiceImpl instance
     * @param userService the user service for loading users
     */
    public UserDirectoryServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the user identified by username
     * @param username the username of the user
     * @return the loaded authenticated user
     */
    private User getUser(String username) {
        if (username == null)
            throw new FileException("The username must not be null to use the files module", null);

        User user = userService.loadUser(username);

        if (user == null)
            throw new FileException("User does not exist, so it's not possible to have a user directory", null);

        return user;
    }

    /**
     * Create the user directory for the given base and user
     * @param base the base the user directory will be rooted at
     * @param user the user to create the directory for
     * @return the path object representing the user directory
     */
    private Path getUserDirectory(Path base, User user) {
        Path userDir = base.resolve(user.getUsername());

        if (!Files.isDirectory(userDir)) {
            try {
                Files.createDirectories(userDir);
            } catch (IOException ex) {
                throw new FileException("Failed to create user's directory", ex);
            }
        }

        return userDir;
    }

    /**
     * Get (and create if needed) the user directory rooted at the provided base
     *
     * @param base the base to root the directory at
     * @param username the username of the user
     * @return the Path object representing the user directory
     */
    @Override
    public Path getUserDirectory(Path base, String username) {
        User user = getUser(username);

        return getUserDirectory(base, user);
    }

    /**
     * Construct the full filepath with the base and the user directory as constructed root, and then look for
     * filename and directory there.
     *
     * @param base      the base the user directory is rooted at
     * @param filename  the name of the file
     * @param directory the directory the file is located in (relative to the user directory)
     * @param username the username of the user
     * @return the Path object representing the created path
     */
    @Override
    public Path createFilePath(Path base, String filename, String directory, String username) {
        User user = getUser(username);
        Path path = getUserDirectory(base, user);

        if (directory != null) {
            path = path.resolve(directory);

            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new FileException("Failed to create directories for path", ex);
            }
        }

        return path.resolve(filename);
    }

    /**
     * Construct the full filepath with the base and the user directory as constructed root, and then look for
     * filename and directory there.
     *
     * @param base      the base the user directory is rooted at
     * @param filename  the name of the file
     * @param directory the directory the file is located in (relative to the user directory)
     * @param user the loaded user
     * @return the Path object representing the created path
     */
    private Path createFilePath(Path base, String filename, String directory, User user) {
        Path path = getUserDirectory(base, user);

        if (directory != null) {
            path = path.resolve(directory);

            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new FileException("Failed to create directories for path", ex);
            }
        }

        return path.resolve(filename);
    }


    /**
     * Determine if the authenticated user can view (i.e. download) the file
     *
     * @param base      the base where the user directory is rooted at
     * @param filename  the name of the file
     * @param directory the directory (will be rooted at /upload-dir/base/user directory path
     * @param username the username of the user
     * @return true if it can be viewed, false if not
     */
    @Override
    public boolean canViewFile(Path base, String filename, String directory, String username) {
        User user = getUser(username);
        String authenticatedUsername = authenticationInformation.getUsername();
        User authenticated = (authenticatedUsername.equals(username)) ? user:getUser(authenticatedUsername);
        Path file = createFilePath(base, filename, directory, user);

        // you can only retrieve a file if it is your own file, or you have the permission to review applications, or it is a profile photo.
        return filename.contains("profile-photo") || file.toString().contains(authenticatedUsername) ||
                authenticated.getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS);
    }

    /**
     * Determine if the authenticated user can delete the file
     *
     * @param base      the base where the user directory is rooted at
     * @param filename  the name of the file
     * @param directory the directory the file is rooted at
     * @param username  the username of the user
     * @return true if the user can delete the file, false if not
     */
    @Override
    public boolean canDeleteFile(Path base, String filename, String directory, String username) {
        User user = getUser(username);
        String authenticatedUsername = authenticationInformation.getUsername();
        User authenticated = (authenticatedUsername.equals(username)) ? user:getUser(authenticatedUsername);
        Path file = createFilePath(base, filename, directory, user);

        // you can only delete a file if it is your own file, or you have the admin permission
        return file.toString().contains(authenticatedUsername) ||
                authenticated.getRole().getPermissions().contains(Permissions.ADMIN);
    }
}
