package ie.ul.ethics.scieng.files.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

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
    @NotNull
    private String target;
    /**
     * The file to upload
     */
    @NotNull
    private MultipartFile file;
}
