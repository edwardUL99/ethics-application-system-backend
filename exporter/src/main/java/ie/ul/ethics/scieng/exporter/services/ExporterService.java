package ie.ul.ethics.scieng.exporter.services;

import ie.ul.ethics.scieng.exporter.ExportedApplication;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * This interface provides a service for exporting applications.
 *
 * A single application can be exported as a single ExportedApplication which gets zipped into a ZIP file containing
 * PDF application and file attachments. If multiple applications, the ZIP file contains folders labelled application ID,
 * each folder containing the application and the file attachments
 *
 * TODO add methods to export the attached files as well
 */
public interface ExporterService {
    /**
     * Export a single application with the given id (only applications that don't have DRAFT status)
     * @param id the ethics application ID
     * @return the exported application
     * @throws IOException if an error occurs
     */
    ExportedApplication exportApplication(String id) throws IOException;

    /**
     * Export all non-DRAFT applications in the system
     * @return the list of exported applications
     * @throws IOException if an error occurs
     */
    List<ExportedApplication> exportApplications() throws IOException;

    /**
     * Export all applications submitted between start and end respectively
     * @param start the start date
     * @param end the end date
     * @return the list of exported applications
     * @throws IOException if an error occurs
     */
    List<ExportedApplication> exportApplications(LocalDate start, LocalDate end) throws IOException;
}
