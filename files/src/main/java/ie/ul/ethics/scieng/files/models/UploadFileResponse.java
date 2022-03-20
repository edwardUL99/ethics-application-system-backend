package ie.ul.ethics.scieng.files.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The response to an upload file request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResponse {
    /**
     * The name of the file uploaded
     */
    private String fileName;
    /**
     * The uri to download the file
     */
    private String downloadUri;
    /**
     * The type of file uploaded
     */
    private String type;
    /**
     * The size of the file
     */
    private long size;
}
