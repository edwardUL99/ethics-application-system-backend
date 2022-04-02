package ie.ul.ethics.scieng.exporter.task;

import ie.ul.ethics.scieng.exporter.email.ExporterEmailService;
import ie.ul.ethics.scieng.exporter.services.ExporterService;
import ie.ul.ethics.scieng.users.models.User;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * This class represents the base export task
 */
public abstract class BaseExportTask implements ExportTask {
    /**
     * The user that requested the task
     */
    protected final User requester;
    /**
     * The timestamp of when the task was created
     */
    protected final LocalDateTime requestedAt;
    /**
     * Email service to notify of export events
     */
    protected final ExporterEmailService emailService;
    /**
     * The service to export applications with
     */
    protected final ExporterService exporterService;
    /**
     * Storage location to store exports
     */
    protected final Path storageLocation;

    /**
     * Create an instance
     * @param requester the user requesting the export
     * @param emailService the service to send export email notifications
     * @param exporterService the service to export with
     * @param storageLocation where exports are stored
     */
    protected BaseExportTask(User requester, ExporterEmailService emailService, ExporterService exporterService, Path storageLocation) {
        this.requester = requester;
        this.emailService = emailService;
        this.exporterService = exporterService;
        this.storageLocation = storageLocation;
        this.requestedAt = LocalDateTime.now();
    }

    /**
     * Get the user that requested the export
     *
     * @return the user that requested the export
     */
    @Override
    public User getRequester() {
        return requester;
    }

    /**
     * Get the timestamp of when the export task was created
     *
     * @return creation timestamp
     */
    @Override
    public LocalDateTime getTimeRequested() {
        return requestedAt;
    }
}
