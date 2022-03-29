package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.Document;
import ie.ul.ethics.scieng.exporter.ExportedApplication;

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
    private final OutputStream outputStream;
    /**
     * List of file attachments to export
     */
    private final List<File> exportedAttachments;

    /**
     * Create an instance
     * @param outputStream the stream the PDF is being written to
     * @param exportedAttachments the list of file attachments to export
     */
    public PDFExportedApplication(OutputStream outputStream, List<File> exportedAttachments) {
        this.outputStream = outputStream;
        this.exportedAttachments = exportedAttachments;
    }

    /**
     * Get the output stream that the application has been exported to
     *
     * @return the output stream
     */
    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    /**
     * Retrieve the list of files representing the attached fjles to export
     *
     * @return the list of files to export in the zip
     */
    @Override
    public List<File> getExportedAttachments() {
        return exportedAttachments;
    }
}
