package ie.ul.ethics.scieng.exporter.services;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.search.ApplicationSpecification;
import ie.ul.ethics.scieng.applications.search.SubmittedApplicationSpecification;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.common.search.SearchParser;
import ie.ul.ethics.scieng.common.zip.Zip;
import ie.ul.ethics.scieng.common.zip.Zippable;
import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.exporter.email.ExporterEmailService;
import ie.ul.ethics.scieng.exporter.pdf.PDFExportedApplication;
import ie.ul.ethics.scieng.exporter.pdf.rendering.ApplicationRenderer;
import ie.ul.ethics.scieng.exporter.serializer.ExportedSerializer;
import ie.ul.ethics.scieng.exporter.task.ExportTask;
import ie.ul.ethics.scieng.exporter.task.RangeExportTask;
import ie.ul.ethics.scieng.exporter.task.SingleExportTask;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.services.FileService;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a service to export applications to PDF
 */
@Service
public class PDFExporterService implements ExporterService {
    /**
     * The service for retrieving applications
     */
    private final ApplicationService applicationService;
    /**
     * The file service to retrieve file paths
     */
    private final FileService fileService;
    /**
     * The location to store artifacts in
     */
    private final Path storageLocation;
    /**
     * The serializer for serializing exported applications into directories on the filesystem
     */
    private final ExportedSerializer serializer;
    /**
     * Service to send export emails
     */
    private final ExporterEmailService emailService;
    /**
     * A temporary directory for carrying out actions
     */
    private Path tempDir;

    /**
     * Create a PDF Exporter Service
     * @param applicationService the service to retrieve applications with
     * @param fileService the service to load files
     * @param serializer the serializer to serialize exported applications with
     * @param emailService the service to send export emails with
     */
    @Autowired
    public PDFExporterService(ApplicationService applicationService, FileService fileService, ExportedSerializer serializer,
                              @Qualifier("exporterEmail") ExporterEmailService emailService) {
        this.applicationService = applicationService;
        this.fileService = fileService;
        this.storageLocation = this.fileService.getStorageLocation();
        this.serializer = serializer;
        this.emailService = emailService;
    }

    /**
     * Render the application to the Input Stream
     * @param application the application to render
     * @return the input stream representing the rendered PDF
     */
    private InputStream renderApplication(Application application) {
        ApplicationRenderer renderer = new ApplicationRenderer(application);
        return renderer.render();
    }

    /**
     * Create the list of files to export for the application
     * @param application the application being exported
     * @return the list of files to export
     */
    private List<File> exportFiles(Application application) {
        List<AttachedFile> attachedFiles = application.getAttachedFiles();
        List<File> files = new ArrayList<>();

        if (attachedFiles != null) {
            for (AttachedFile attachedFile : attachedFiles) {
                try {
                    Resource resource = fileService.loadFile(attachedFile.getFilename(), attachedFile.getDirectory(), attachedFile.getUsername());

                    if (resource != null)
                        files.add(resource.getFile());
                } catch (FileException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return files;
    }


    /**
     * The internal method to export the application
     * @param application the application to export
     * @return the exported application
     */
    private ExportedApplication export(Application application) {
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            InputStream inputStream = renderApplication(application);

            return new PDFExportedApplication(inputStream, exportFiles(application), application);
        } else {
            return null;
        }
    }

    /**
     * Export a single application with the given id (only applications that don't have DRAFT status)
     *
     * @param id the ethics application ID
     * @return the exported application
     */
    @Override
    public ExportedApplication exportApplication(String id) {
        Application application = this.applicationService.getApplication(id);

        if (application == null) {
            return null;
        } else {
            return export(application);
        }
    }

    /**
     * Export the applications in the given list
     * @param applications the list of applications to export
     * @return the list of exported applications
     */
    private List<ExportedApplication> exportApplications(List<Application> applications) {
        List<ExportedApplication> exported = new ArrayList<>();

        for (Application application : applications)
            exported.add(export(application));

        return exported;
    }

    /**
     * Export all applications submitted between start and end respectively
     *
     * @param start the start date
     * @param end   the end date
     * @return the list of exported applications
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ExportedApplication> exportApplications(LocalDate start, LocalDate end)  {
        Class<? extends ApplicationSpecification> specificationClass = SubmittedApplicationSpecification.class;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = start.format(formatter);
        String endDate = end.format(formatter);
        String query = String.format("submittedTime>%s,submittedTime<%s", startDate, endDate);

        Specification<Application> specification = new SearchParser<>(specificationClass).parse(query, ApplicationSpecification.OPERATION_PATTERN, false);

        return exportApplications(applicationService.search(specification));
    }

    /**
     * Create the temp directory if not already created
     * @return the temp dir path
     * @throws IOException if an error occurs
     */
    private Path getTempDir() throws IOException {
        if (tempDir == null)
            tempDir = Files.createTempDirectory("ethics_temp");

        return tempDir;
    }

    /**
     * ZIP any attachments that may exist
     * @param exportedDirectory the directory of the exported application
     */
    private void zipAttachments(Path exportedDirectory) throws IOException {
        Path attachments = exportedDirectory.resolve("attachments");

        if (Files.exists(attachments) && Files.list(attachments).findAny().isPresent()) {
            tempDir = getTempDir().resolve("attachments");

            if (Files.exists(tempDir) && Files.list(tempDir).findAny().isPresent())
                Files.delete(tempDir);

            Path moved = Files.move(attachments, tempDir, StandardCopyOption.REPLACE_EXISTING);
            Zip.zipFile(exportedDirectory.resolve("attachments.zip").toString(), moved.toFile());
        }
    }

    /**
     * Export the application to ZIP
     *
     * @param exportedApplication the application to export
     * @param name the name of the zip
     * @return the file representing the exported ZIP
     * @throws IOException if an error occurs
     */
    @Override
    public File exportToZip(ExportedApplication exportedApplication, String name) throws IOException {
        File file = serializer.saveToDisk(exportedApplication, exportedApplication.getApplication().getApplicationId(), null);
        Path path = file.toPath();
        zipAttachments(path);

        return Zip.zipFile(name, file);
    }

    /**
     * Exports multiple applications to a single ZIP
     *
     * @param exportedApplications the applications to export to a single zip
     * @param parentDirectory      the parent directory to save all the exported applications to
     * @param name                 the name of the zip
     * @return the file representing the exported ZIP
     * @throws IOException if an error occurs
     */
    @Override
    public File exportMultipleToZip(List<ExportedApplication> exportedApplications, String parentDirectory, String name) throws IOException {
        List<Zippable> toZip = new ArrayList<>();
        Path parentPath = Path.of(parentDirectory);

        for (ExportedApplication exported : exportedApplications) {
            String applicationId = exported.getApplication().getApplicationId();
            File file = serializer.saveToDisk(exported, applicationId, parentDirectory);
            Path path = file.toPath().resolve(applicationId);
            zipAttachments(path);

            toZip.add(Zip.createZippable(new File(parentPath.resolve(applicationId).toString())));
        }

        return Zip.zipFiles(name, toZip);
    }

    /**
     * Create an export task that exports a single application by ID
     *
     * @param id        the ID of the application
     * @param requester the user requesting the export
     * @return the task
     */
    @Override
    public ExportTask createTask(String id, User requester) {
        return new SingleExportTask(requester, emailService, this, storageLocation, id);
    }

    /**
     * Create an export task that exports applications submitted within the date range (dates in YYYY-MM-DD)
     *
     * @param start     the start date
     * @param end       the end date
     * @param requester the user requesting the export
     * @return the task
     */
    @Override
    public ExportTask createTask(String start, String end, User requester) {
        return new RangeExportTask(requester, emailService, this, storageLocation, start, end);
    }
}
