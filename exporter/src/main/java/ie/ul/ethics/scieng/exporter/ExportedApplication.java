package ie.ul.ethics.scieng.exporter;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * This interface represents an exported application.
 * TODO as of now just a marker, think of methods it may need like get output stream etc.
 */
public interface ExportedApplication {
    /**
     * Get the output stream that the application has been exported to
     * @return the output stream
     */
    OutputStream getOutputStream();

    /**
     * Retrieve the list of files representing the attached fjles to export
     * @return the list of files to export in the zip
     */
    List<File> getExportedAttachments();
}
