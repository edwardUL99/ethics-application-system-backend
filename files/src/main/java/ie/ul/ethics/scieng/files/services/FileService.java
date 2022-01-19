package ie.ul.ethics.scieng.files.services;

import ie.ul.ethics.scieng.files.exceptions.FileException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

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
     * @throws FileException if an error occurs
     */
    String storeFile(MultipartFile file, String directory, String target) throws FileException;

    /**
     * Load the file from upload dir.
     * @param filename the name of the file to load
     * @return the loaded file as a resource, null if not found
     * @throws FileException if an error occurs
     */
    Resource loadFile(String filename) throws FileException;
}