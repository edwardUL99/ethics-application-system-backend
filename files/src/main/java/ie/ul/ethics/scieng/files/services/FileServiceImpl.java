package ie.ul.ethics.scieng.files.services;

import ie.ul.ethics.scieng.files.config.FilesConfigurationProperties;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.exceptions.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This is the implementing class of FileService
 */
@Service
public class FileServiceImpl implements FileService {
    /**
     * The path object representing the storage location
     */
    private final Path storageLocation;
    /**
     * The service for managing a user's directory
     */
    private final UserDirectoryService userDirectoryService;

    /**
     * Construct a FileService with the provided properties
     * @param properties the properties configuration
     * @param userDirectoryService the service for managing a user's directory
     */
    @Autowired
    public FileServiceImpl(FilesConfigurationProperties properties, UserDirectoryService userDirectoryService) {
        this.storageLocation = Paths.get(properties.getStorageDir())
                .toAbsolutePath().normalize();
        createStorageLocation(this.storageLocation);
        this.userDirectoryService = userDirectoryService;
    }

    /**
     * Create the storage location if it does not exist
     */
    private void createStorageLocation(Path storageLocation) {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException ex) {
            throw new FileException("Could not create the upload directory", ex);
        }
    }

    /**
     * Store the file in the provided target. Upload dir is always prepended to the target path
     *
     * @param file   the file to store
     * @param directory the directory to create the file in
     * @param target the target file name
     * @param username the username of the user storing the file
     * @return the name of the saved file
     * @throws FileException if an error occurs
     */
    @Override
    public String storeFile(MultipartFile file, String directory, String target, String username) throws FileException {
        String name = file.getOriginalFilename();

        if (name == null)
            throw new FileException("Illegal File. getOriginalFilename returned null", null);

        name = StringUtils.cleanPath(file.getOriginalFilename());

        if (name.contains(".."))
            throw new FileException("You cannot include .. in the file path", null);

        try {
            Path storageLocation = this.storageLocation.resolve("data");
            createStorageLocation(storageLocation);

            String targetPath = (directory == null) ? target:directory + "/" + target;

            Path path = userDirectoryService.createFilePath(storageLocation, target, directory, username);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return targetPath;
        } catch (IOException ex) {
            throw new FileException("Failed to store file", ex);
        }
    }

    /**
     * Load the file from upload dir.
     *
     * @param filename the name of the file to load
     * @return the loaded file as a resource, null if not found
     * @param username the username of the user viewing the file
     * @throws FileException if an error occurs
     */
    @Override
    public Resource loadFile(String filename, String directory, String username) throws FileException {
        Path storageLocation = this.storageLocation.resolve("data");

        if (!userDirectoryService.canViewFile(storageLocation, filename, directory, username))
            throw new PermissionDeniedException("The user cannot view the file", null);

        try {
            Path path = userDirectoryService.createFilePath(storageLocation, filename, directory, username);

            Resource resource = new UrlResource(path.toUri());

            return (resource.exists()) ? resource:null;
        } catch (MalformedURLException ex) {
            throw new FileException("Failed to load file", ex);
        }
    }

    /**
     * Delete the file with filename and directory from the filesystem
     *
     * @param filename  the name of the file
     * @param directory the directory the file is contained in
     * @param username the username of the user deleting
     * @throws FileException if an error occurs
     */
    @Override
    public void deleteFile(String filename, String directory, String username) throws FileException {
        Path storageLocation = this.storageLocation.resolve("data");

        if (!userDirectoryService.canDeleteFile(storageLocation, filename, directory, username))
            throw new PermissionDeniedException("The user cannot delete this file", null);

        Path file = userDirectoryService.createFilePath(storageLocation, filename, directory, username);

        try {
            Files.delete(file);
            Path parent = file.getParent();
            File[] files = parent.toFile().listFiles();

            if (files != null && files.length == 0)
                Files.delete(parent);
        } catch (IOException ex) {
            throw new FileException("Failed to delete file " + file, ex);
        }
    }

    /**
     * Get the path representing where files are stored on the server
     *
     * @return the storage location path
     */
    @Override
    public Path getStorageLocation() {
        createStorageLocation(this.storageLocation);
        return storageLocation;
    }
}
