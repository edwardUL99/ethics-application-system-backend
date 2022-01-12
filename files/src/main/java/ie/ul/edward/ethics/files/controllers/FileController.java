package ie.ul.edward.ethics.files.controllers;

import ie.ul.edward.ethics.files.antivirus.AntivirusScanner;
import ie.ul.edward.ethics.files.config.FilesConfigurationProperties;
import ie.ul.edward.ethics.files.exceptions.FileException;
import ie.ul.edward.ethics.files.models.UploadFileRequest;
import static ie.ul.edward.ethics.common.Constants.*;

import ie.ul.edward.ethics.files.models.UploadFileResponse;
import ie.ul.edward.ethics.files.services.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.capybara.clamav.ClamavException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * This controller represents the controller for uploading and downloading files
 */
@RestController
@RequestMapping("/api/files")
@Log4j2
public class FileController {
    /**
     * The file service for uploading and downloading files
     */
    private final FileService fileService;
    /**
     * The list of supported MIME types
     */
    private final List<String> supportedTypes;
    /**
     * The scanner for antivirus in uploaded files
     */
    private final AntivirusScanner antivirusScanner;

    /**
     * Create the controller with the provided file service
     * @param fileService the file service for uploading and downloading files
     * @param properties the configuration properties for the files module
     * @param antivirusScanner the scanner for antivirus in uploaded files
     */
    public FileController(FileService fileService, FilesConfigurationProperties properties, AntivirusScanner antivirusScanner) {
        this.fileService = fileService;
        this.supportedTypes = properties.getSupportedTypes();
        this.antivirusScanner = antivirusScanner;

        if (!antivirusScanner.isEnabled())
            log.warn("Antivirus scanning is disabled. Uploaded files will not be scanned for viruses. This is dangerous and " +
                    "not recommended!");
        else
            log.info("Antivirus scanning provided by {}. Uploaded files will be scanned for viruses and rejected if they contain any",
                    antivirusScanner);
    }

    /**
     * Resolves the uploaded file name into directory and filename (0 = directory, 1 = filename)
     * @param fileName the full path to resolve
     * @return the array with directory at 0 and filename at 1
     */
    private String[] resolveUploadedFilename(String fileName) {
        String[] fileNameParts = fileName.split("/");

        String directory;
        if (fileNameParts.length > 1) {
            StringBuilder directoryBuilder = new StringBuilder();
            for (int i = 0; i < fileNameParts.length - 1; i++)
                directoryBuilder.append(fileNameParts[i]).append("/");

            directory = directoryBuilder.toString();

            if (directory.endsWith("/"))
                directory = directory.substring(0, directory.length() - 1);

            fileName = fileNameParts[fileNameParts.length - 1];
        } else {
            directory = null;
        }

        return new String[]{directory, fileName};
    }

    /**
     * This is the endpoint for uploading files
     * @param request the request for uploading the file
     * @return the response body
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@ModelAttribute @Valid UploadFileRequest request) {
        try {
            MultipartFile file = request.getFile();

            if (!antivirusScanner.isFileSafe(file.getInputStream())) {
                return respondError(VIRUS_FOUND_FILE);
            } else if (!supportedTypes.contains(file.getContentType())) {
                return respondError(UNSUPPORTED_FILE_TYPE);
            }

            String fileName = fileService.storeFile(file, request.getDirectory(), request.getTarget());
            String[] resolved = resolveUploadedFilename(fileName);
            String directory = resolved[0];
            fileName = resolved[1];

            String uri = "/api/files/download/" + fileName;

            if (directory != null)
                uri += "?directory=" + directory;

            return ResponseEntity.ok(new UploadFileResponse(fileName, uri, file.getContentType(), file.getSize()));
        } catch (FileException | IOException | ClamavException ex) {
            ex.printStackTrace();
            return respondError(FILE_ERROR);
        }
    }

    /**
     * The endpoint for downloading a file
     * @param filename the name of the file to download
     * @param directory the directory to retrieve the file from
     * @param request the request object
     * @return the response body
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename, @RequestParam(required = false) String directory, HttpServletRequest request) {
        try {
            if (directory != null)
                filename = directory + "/" + filename;
            Resource resource = fileService.loadFile(filename);

            if (resource == null)
                return ResponseEntity.notFound().build();

            String contentType;

            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (FileException ex) {
            return respondError(FILE_ERROR);
        }
    }
}
