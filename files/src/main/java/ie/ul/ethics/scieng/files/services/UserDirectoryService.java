package ie.ul.ethics.scieng.files.services;

import java.nio.file.Path;

/**
 * This interface represents a service providing utilities for working with user directories
 */
public interface UserDirectoryService {
    /**
     * Get (and create if needed) the user directory for the given base and username
     * @param base the base path that roots the user directory
     * @param username the username of the user
     * @return the Path object representing the user directory
     */
    Path getUserDirectory(Path base, String username);

    /**
     * Construct the full filepath with the base and the user directory as constructed root, and then look for
     * filename and directory there.
     * @param base the base the user directory is rooted at
     * @param filename the name of the file
     * @param directory the directory the file is located in (relative to the user directory)
     * @param username the username of the user to get the file path for
     * @return the Path object representing the created path
     */
    Path createFilePath(Path base, String filename, String directory, String username);

    /**
     * Determine if the user can view (i.e. download) the file
     * @param base the base where the user directory is rooted at
     * @param filename the name of the file
     * @param directory the directory (will be rooted at /upload-dir/base/user directory path
     * @param username the username of the user viewing
     * @return true if it can be viewed, false if not
     */
    boolean canViewFile(Path base, String filename, String directory, String username);

    /**
     * Determine if the authenticated user can delete the file
     * @param base the base where the user directory is rooted at
     * @param filename the name of the file
     * @param directory the directory the file is rooted at
     * @param username the username of the user wishing to delete the file
     * @return true if the user can delete the file, false if not
     */
    boolean canDeleteFile(Path base, String filename, String directory, String username);
}
