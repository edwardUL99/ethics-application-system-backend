package ie.ul.edward.ethics.files.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * This request represents a request to upload a file
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileRequest {
    /**
     * The directory within the defined uploads directory to store the file
     */
    private String directory;
    /**
     * The target file name
     */
    private String target;
    /**
     * The file to upload
     */
    private MultipartFile file;
}
