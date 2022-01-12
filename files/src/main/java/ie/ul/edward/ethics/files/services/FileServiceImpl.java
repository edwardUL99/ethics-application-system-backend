package ie.ul.edward.ethics.files.services;

import ie.ul.edward.ethics.files.config.FilesConfigurationProperties;
import ie.ul.edward.ethics.files.exceptions.FileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
     * Construct a FileService with the provided properties
     * @param properties the properties configuration
     */
    @Autowired
    public FileServiceImpl(FilesConfigurationProperties properties) {
        this.storageLocation = Paths.get(properties.getStorageDir())
                .toAbsolutePath().normalize();
        createStorageLocation();
    }

    /**
     * Create the storage location if it does not exist
     */
    private void createStorageLocation() {
        try {
            Files.createDirectories(this.storageLocation);
        } catch (Exception ex) {
            throw new FileException("Could not create the upload directory", ex);
        }
    }

    /**
     * Store the file in the provided target. Upload dir is always prepended to the target path
     *
     * @param file   the file to store
     * @param directory the directory to create the file in
     * @param target the target file name
     * @return the name of the saved file
     * @throws FileException if an error occurs
     */
    @Override
    public String storeFile(MultipartFile file, String directory, String target) throws FileException {
        String name = file.getOriginalFilename();

        if (name == null)
            throw new FileException("Illegal File. getOriginalFilename returned null", null);

        name = StringUtils.cleanPath(file.getOriginalFilename());

        if (name.contains(".."))
            throw new FileException("You cannot include .. in the file path", null);

        try {
            createStorageLocation();

            Path targetFile;
            String targetPath;

            if (directory == null) {
                targetFile = this.storageLocation.resolve(target);
                targetPath = target;
            } else {
                Path dir = this.storageLocation.resolve(directory);
                Files.createDirectories(dir);
                targetFile = dir.resolve(target);
                targetPath = directory + "/" + target;
            }

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

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
     * @throws FileException if an error occurs
     */
    @Override
    public Resource loadFile(String filename) throws FileException {
        try {
            Path filePath = storageLocation.resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            return (resource.exists()) ? resource:null;
        } catch (MalformedURLException ex) {
            throw new FileException("Failed to load file", ex);
        }
    }
}
