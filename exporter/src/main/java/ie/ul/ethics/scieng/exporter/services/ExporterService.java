package ie.ul.ethics.scieng.exporter.services;

import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.exporter.task.ExportTask;
import ie.ul.ethics.scieng.users.models.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * This interface provides a service for exporting applications.
 *
 * A single application can be exported as a single ExportedApplication which gets zipped into a ZIP file containing
 * PDF application and file attachments. If multiple applications, the ZIP file contains folders labelled application ID,
 * each folder containing the application and the file attachments
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
     * Export all applications submitted between start and end respectively
     * @param start the start date
     * @param end the end date
     * @return the list of exported applications
     * @throws IOException if an error occurs
     */
    List<ExportedApplication> exportApplications(LocalDate start, LocalDate end) throws IOException;

    /**
     * Export the application to ZIP
     * @param exportedApplication the application to export
     * @param name the name of the zip
     * @return the file representing the exported ZIP
     * @throws IOException if an error occurs
     */
    File exportToZip(ExportedApplication exportedApplication, String name) throws IOException;

    /**
     * Exports multiple applications to a single ZIP
     * @param exportedApplications the applications to export to a single zip
     * @param parentDirectory      the parent directory to save all the exported applications to
     * @param name the name of the ZIP
     * @return the file representing the exported ZIP
     * @throws IOException if an error occurs
     */
    File exportMultipleToZip(List<ExportedApplication> exportedApplications, String parentDirectory, String name) throws IOException;

    /**
     * Create an export task that exports a single application by ID
     * @param id the ID of the application
     * @param requester the user requesting the export
     * @return the task
     */
    ExportTask createTask(String id, User requester);

    /**
     * Create an export task that exports applications submitted within the date range (dates in YYYY-MM-DD)
     * @param start the start date
     * @param end the end date
     * @param requester the user requesting the export
     * @return the task
     */
    ExportTask createTask(String start, String end, User requester);
}
