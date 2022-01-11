package ie.ul.edward.ethics.files.controllers;

import ie.ul.edward.ethics.files.exceptions.FileException;
import ie.ul.edward.ethics.files.models.UploadFileRequest;
import static ie.ul.edward.ethics.common.Constants.*;

import ie.ul.edward.ethics.files.models.UploadFileResponse;
import ie.ul.edward.ethics.files.services.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller represents the controller for uploading and downloading files
 */
@RestController
@RequestMapping("/api/files")
public class FileController {
    /**
     * The file service for uploading and downloading files
     */
    private final FileService fileService;

    /**
     * Create the controller with the provided file service
     * @param fileService the file service for uploading and downloading files
     */
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * This is the endpoint for uploading files
     * @param request the request for uploading the file
     * @return the response body
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@ModelAttribute UploadFileRequest request) {
        try {
            MultipartFile file = request.getFile();
            String fileName = fileService.storeFile(file, request.getDirectory(), request.getTarget());

            String directory;
            if (fileName.contains("/")) {
                int index = fileName.indexOf("/");
                directory = fileName.substring(0, index);
                fileName = fileName.substring(index + 1);
            } else {
                directory = null;
            }

            String uri = "/api/files/download/" + fileName;

            if (directory != null)
                uri += "?directory=" + directory;

            return ResponseEntity.ok(new UploadFileResponse(fileName, uri, file.getContentType(), file.getSize()));
        } catch (FileException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERROR, FILE_ERROR);

            return ResponseEntity.badRequest().body(response);
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
            Map<String, Object> response = new HashMap<>();
            response.put(ERROR, FILE_ERROR);

            return ResponseEntity.badRequest().body(response);
        }
    }
}
