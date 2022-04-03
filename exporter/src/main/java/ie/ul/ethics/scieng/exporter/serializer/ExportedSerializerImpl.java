package ie.ul.ethics.scieng.exporter.serializer;

import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class provides the default implementation of the exported serializer
 */
@Service
public class ExportedSerializerImpl implements ExportedSerializer {
    /**
     * Path representing location to store files at
     */
    private final Path storageLocation;

    /**
     * Create an instance
     * @param fileService used to query storage information
     */
    @Autowired
    public ExportedSerializerImpl(FileService fileService) {
        this.storageLocation = fileService.getStorageLocation();
    }

    /**
     * Create the directory represented by the given path if it doesn't exist
     * @param directory the directory to create
     */
    private void createDirectory(Path directory) {
        try {
            if (!Files.isDirectory(directory) && !Files.isRegularFile(directory))
                Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new FileException("Failed to create directory", ex);
        }
    }

    /**
     * Export the application PDF
     * @param filename the name of the pdf
     * @param storage the storage directory
     * @param inputStream the stream to write to the file
     */
    private void exportPDF(String filename, Path storage, InputStream inputStream) {
        try {
            Path path = storage.resolve(filename);
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FileException("Failed to write PDF", ex);
        }
    }

    /**
     * Export the attachments to the storage path
     * @param attachments the list of attachments
     * @param storage the storage location of the exported application
     */
    private void exportAttachments(List<File> attachments, Path storage) {
        if (attachments.size() > 0) {
            storage = storage.resolve("attachments");
            createDirectory(storage);

            try {
                for (File attachment : attachments) {
                    Path source = attachment.toPath();
                    Path destination = storage.resolve(source.getFileName());

                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                throw new FileException("Failed to export attachments", ex);
            }
        }
    }

    /**
     * Save the exported application under the given name
     *
     * @param exported  the application to export
     * @param name      the name to export the application under. If null, the application ID is used
     * @param directory a directory to store the folder under. If null, it's not stored under that directory, instead stored
     *                  in the root of the upload directory
     * @return the file representing the directory the application is stored in (or parent directory if specified)
     */
    @Override
    public File saveToDisk(ExportedApplication exported, String name, String directory) {
        Path exports = storageLocation.resolve("exports");
        createDirectory(exports);

        Path returned;
        Path storage = exports;

        if (directory != null)
            storage = storage.resolve(directory);

        returned = storage;

        String id = exported.getApplication().getApplicationId();

        name = (name == null) ? id:name;
        storage = storage.resolve(name);

        if (directory == null)
            returned = storage;

        createDirectory(storage);
        exportPDF(String.format("%s.pdf", id), storage, exported.getInputStream());
        exportAttachments(exported.getExportedAttachments(), storage);

        return returned.toFile();
    }
}
