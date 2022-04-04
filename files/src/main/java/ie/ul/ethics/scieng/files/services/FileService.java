package ie.ul.ethics.scieng.files.services;

import ie.ul.ethics.scieng.files.exceptions.FileException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * This interface represents a service for uploading and downloading files
 */
public interface FileService {
    /**
     * Store the file in the provided target. Upload dir is always prepended to the target path
     * @param file the file to store
     * @param directory the directory within the uploads to store target. If null, just save it to the root
     * @param target the target file name
     * @return the name of the saved file
     * @param username the username of the user storing the file
     * @throws FileException if an error occurs
     */
    String storeFile(MultipartFile file, String directory, String target, String username) throws FileException;

    /**
     * Load the file from upload dir.
     * @param filename the name of the file to load
     * @param directory the directory of the file
     * @return the loaded file as a resource, null if not found
     * @param username the username of the user storing the file
     * @throws FileException if an error occurs
     */
    Resource loadFile(String filename, String directory, String username) throws FileException;

    /**
     * Delete the file with filename and directory from the filesystem
     * @param filename the name of the file
     * @param directory the directory the file is contained in
     * @param username the username of the user storing the file
     * @throws FileException if an error occurs
     */
    void deleteFile(String filename, String directory, String username) throws FileException;

    /**
     * Get the path representing where files are stored on the server
     * @return the storage location path
     */
    Path getStorageLocation();
}
