package ie.ul.ethics.scieng.exporter;

import ie.ul.ethics.scieng.applications.models.applications.Application;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    ByteArrayOutputStream getOutputStream();

    /**
     * Retrieve the list of files representing the attached files to export
     * @return the list of files to export in the zip
     */
    List<File> getExportedAttachments();

    /**
     * Get the application being exported
     * @return application being exported
     */
    Application getApplication();
}
