package ie.ul.ethics.scieng.exporter.pdf;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.exporter.ExportedApplication;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * This class represents an application exported to PDF
 */
public class PDFExportedApplication implements ExportedApplication {
    /**
     * The stream to read the PDF with
     */
    private final InputStream inputStream;
    /**
     * List of file attachments to export
     */
    private final List<File> exportedAttachments;
    /**
     * Application being exported
     */
    private final Application application;

    /**
     * Create an instance
     * @param inputStream the stream the PDF is being written to
     * @param exportedAttachments the list of file attachments to export
     * @param application the application being exported
     */
    public PDFExportedApplication(InputStream inputStream, List<File> exportedAttachments, Application application) {
        this.inputStream = inputStream;
        this.exportedAttachments = exportedAttachments;
        this.application = application;
    }

    /**
     * Get the input stream that the application has been exported to
     *
     * @return the input stream
     */
    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Retrieve the list of files representing the attached files to export
     *
     * @return the list of files to export in the zip
     */
    @Override
    public List<File> getExportedAttachments() {
        return exportedAttachments;
    }

    /**
     * Get the application being exported
     *
     * @return application being exported
     */
    @Override
    public Application getApplication() {
        return application;
    }
}
