package ie.ul.ethics.scieng.exporter.task;

import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.exporter.email.ExporterEmailService;
import ie.ul.ethics.scieng.exporter.services.ExporterService;
import ie.ul.ethics.scieng.users.models.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents a task to perform an export of a single application
 */
public class SingleExportTask extends BaseExportTask {
    /**
     * The ID of the application to export
     */
    private final String id;

    /**
     * Create an instance
     *
     * @param requester       the user requesting the export
     * @param emailService    the service to send export email notifications
     * @param exporterService the service to export with
     * @param id              the ID to export the application with
     * @param storageLocation where exports are stored
     */
    public SingleExportTask(User requester, ExporterEmailService emailService, ExporterService exporterService, Path storageLocation, String id) {
        super(requester, emailService, exporterService, storageLocation);
        this.id = id;
    }

    /**
     * Get the result of the export task. The result is the file represented the exported application(s).
     * Only makes sense calling after {@link #execute()}
     *
     * @return the result.
     */
    @Override
    public File getResult() {
        return new File(storageLocation.resolve("exports").resolve(id + ".zip").toString());
    }

    /**
     * Performs the export to ZIP asynchronously
     * @param exported application to export
     */
    private void doExport(ExportedApplication exported) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            onStarted();

            try {
                File saved = exporterService.exportToZip(exported, getResult().getAbsolutePath());
                emailService.sendExportLinkEmail(requester, saved.getName(), requestedAt);
                onCompleted();
            } catch (IOException ex) {
                ex.printStackTrace();
                emailService.sendExportFailedEmail(requester, requestedAt);
                onFail();
            }
        });
    }

    /**
     * Execute the export task
     *
     * @return true if successful, false if not
     * @throws IOException if an error occurs
     */
    @Override
    public boolean execute() throws IOException {
        onPending();
        ExportedApplication exported = exporterService.exportApplication(id);

        if (exported == null) {
            return false;
        } else {
            doExport(exported);

            return true;
        }
    }
}
