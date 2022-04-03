package ie.ul.ethics.scieng.exporter.task;

import ie.ul.ethics.scieng.exporter.email.ExporterEmailService;
import ie.ul.ethics.scieng.exporter.services.ExporterService;
import ie.ul.ethics.scieng.users.models.User;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * This class represents the base export task
 */
@Log4j2
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

    /**
     * Log that the export task has transitioned state
     * @param status the status of the task to log
     */
    protected void logTask(TaskStatus status) {
        log.info("Export Task requested by {} at {} has transitioned to state: {}", requester.getUsername(),
                requestedAt, status.label);
    }

    /**
     * A pending hook that by default logs, but can be overridden
     */
    protected void onPending() {
        logTask(TaskStatus.PENDING);
    }

    /**
     * A started hook that by default logs, but can be overridden
     */
    protected void onStarted() {
        logTask(TaskStatus.STARTED);
    }

    /**
     * A completed hook that by default logs, but can be overridden
     */
    protected void onCompleted() {
        logTask(TaskStatus.COMPLETED);
    }

    /**
     * A failure hook that by default logs, but can be overridden
     */
    protected void onFail() {
        logTask(TaskStatus.FAILED);
    }

    /**
     * An enum for use in logging
     */
    protected enum TaskStatus {
        /**
         * Task is pending being run by a thread
         */
        PENDING("Pending"),
        /**
         * Task has started
         */
        STARTED("Started"),
        /**
         * Task has completed
         */
        COMPLETED("Completed"),
        /**
         * Task has failed
         */
        FAILED("Failed");

        /**
         * The task status label
         */
        private final String label;

        /**
         * Create the enum value
         * @param label the label to display in logs
         */
        TaskStatus(String label) {
            this.label = label;
        }
    }
}
