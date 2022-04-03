package ie.ul.ethics.scieng.exporter.controllers;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.exporter.services.ExporterService;
import ie.ul.ethics.scieng.exporter.task.ExportTask;
import ie.ul.ethics.scieng.files.services.FileService;
import static ie.ul.ethics.scieng.common.Constants.*;

import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/export")
public class ExporterController {
    /**
     * Service for exporting applications
     */
    private final ExporterService exporterService;
    /**
     * The path to the storage location
     */
    private final Path storageLocation;
    /**
     * Service for loading users
     */
    private final UserService userService;
    /**
     * Information of the authenticated user
     */
    @javax.annotation.Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * Construct a controller instance
     * @param exporterService the service for exporting applications
     * @param fileService the service for querying storage locations
     */
    public ExporterController(ExporterService exporterService, FileService fileService, UserService userService) {
        this.exporterService = exporterService;
        this.storageLocation = fileService.getStorageLocation();
        this.userService = userService;
    }

    /**
     * Endpoint for downloading the zip file
     * @param filename the name of the ZIP file to download
     * @return the response body
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> download(@PathVariable String filename) {
        Path exported = storageLocation.resolve("exports").resolve(filename);

        if (Files.isRegularFile(exported)) {
            Resource resource = new FileSystemResource(exported);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/zip");
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Export a single application with the given ID
     * @param id the ID of the application to export
     * @return response body
     */
    @PostMapping("/single")
    public ResponseEntity<?> export(@RequestParam String id) {
        try {
            User requester = userService.loadUser(authenticationInformation.getUsername());

            if (requester == null)
                return respondError(INSUFFICIENT_PERMISSIONS);

            ExportTask task = exporterService.createTask(id, requester);

            if (task.execute()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return respondError(EXPORT_FAILED);
        }
    }

    /**
     * Export applications within the date range
     * @param start the start date in format YYYY-MM-DD
     * @param end the end date in format YYYY-MM-DD
     * @return the response body
     */
    @PostMapping("/range")
    public ResponseEntity<?> exportRange(@RequestParam String start, @RequestParam String end) {
        try {
            User requester = userService.loadUser(authenticationInformation.getUsername());

            if (requester == null)
                return respondError(INSUFFICIENT_PERMISSIONS);

            ExportTask task = exporterService.createTask(start, end, requester);

            if (task.execute()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return respondError(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            return respondError(EXPORT_FAILED);
        }
    }
}
