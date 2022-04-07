package ie.ul.ethics.scieng.exporter.task;

import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.exporter.email.ExporterEmailService;
import ie.ul.ethics.scieng.exporter.services.ExporterService;
import ie.ul.ethics.scieng.users.models.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A task that can export applications within a date range
 */
public class RangeExportTask extends BaseExportTask {
    /**
     * The start date in YYYY-MM-DD format
     */
    private final String start;
    /**
     * The end date in YYYY-MM-DD format
     */
    private final String end;

    /**
     * Create an instance
     *
     * @param requester       the user requesting the export
     * @param emailService    the service to send export email notifications
     * @param exporterService the service to export with
     * @param storageLocation where exports are stored
     * @param start           the start date in YYYY-MM-DD format
     * @param end             the end date in YYYY-MM-DD format
     */
    public RangeExportTask(User requester, ExporterEmailService emailService, ExporterService exporterService, Path storageLocation,
                              String start, String end) {
        super(requester, emailService, exporterService, storageLocation);
        this.start = start;
        this.end = end;
    }

    /**
     * Get the result of the export task. The result is the file represented the exported application(s).
     * Only makes sense calling after {@link #execute()}
     *
     * @return the result.
     */
    @Override
    public File getResult() {
        String parentDir = String.format("%s_to_%s", start, end);
        String name = parentDir + ".zip";
        name = storageLocation.resolve("exports").resolve(name).toString();

        return new File(name);
    }

    /**
     * Asynchronously perform the export
     * @param exported the list of applications to export
     */
    private void doExport(List<ExportedApplication> exported) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            onStarted();

            try {
                Path parent = storageLocation.resolve("exports").resolve(String.format("%s_to_%s", start, end));

                Files.createDirectories(parent);
                String name = parent + ".zip";
                name = storageLocation.resolve("exports").resolve(name).toString();

                File saved = exporterService.exportMultipleToZip(exported, parent.toString(), name);
                emailService.sendExportLinkEmail(requester, saved.getName(), requestedAt);
                onCompleted();
            } catch (IOException ex) {
                onFail();
                ex.printStackTrace();
                emailService.sendExportFailedEmail(requester, requestedAt);
            }
        });
    }

    /**
     * Execute the export task
     *
     * @return true if successful, false if not
     * @throws DateTimeParseException if the dates fail to be parsed
     */
    @Override
    public boolean execute() {
        onPending();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        try {
            List<ExportedApplication> exported = exporterService.exportApplications(startDate, endDate);

            if (exported.size() == 0) {
                return false;
            } else {
                doExport(exported);

                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            onFail();
            emailService.sendExportFailedEmail(requester, requestedAt);

            return false;
        }
    }
}
