package ie.ul.ethics.scieng.exporter.pdf;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.exporter.ExportedApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * This class represents an application exported to PDF
 */
public class PDFExportedApplication implements ExportedApplication {
    /**
     * The stream the PDF is being written to
     */
    private final ByteArrayOutputStream outputStream;
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
     * @param outputStream the stream the PDF is being written to
     * @param exportedAttachments the list of file attachments to export
     * @param application the application being exported
     */
    public PDFExportedApplication(ByteArrayOutputStream outputStream, List<File> exportedAttachments, Application application) {
        this.outputStream = outputStream;
        this.exportedAttachments = exportedAttachments;
        this.application = application;
    }

    /**
     * Get the output stream that the application has been exported to
     *
     * @return the output stream
     */
    @Override
    public ByteArrayOutputStream getOutputStream() {
        return this.outputStream;
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
