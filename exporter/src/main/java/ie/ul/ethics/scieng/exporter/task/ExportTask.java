package ie.ul.ethics.scieng.exporter.task;

import ie.ul.ethics.scieng.users.models.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * This interface represents a task to export a single or multiple applications
 */
public interface ExportTask {
    /**
     * Get the user that requested the export
     * @return the user that requested the export
     */
    User getRequester();

    /**
     * Get the timestamp of when the export task was created
     * @return creation timestamp
     */
    LocalDateTime getTimeRequested();

    /**
     * Get the result of the export task. The result is the file represented the exported application(s).
     * Only makes sense calling after {@link #execute()}
     * @return the result.
     */
    File getResult();

    /**
     * Execute the export task
     * @return true if successful, false if not because a resource was not found (no applications found). Doesn't indicate
     * if the whole operation succeeds as it runs asynchronously
     * @throws IOException if an error occurs
     */
    boolean execute() throws IOException;
}
