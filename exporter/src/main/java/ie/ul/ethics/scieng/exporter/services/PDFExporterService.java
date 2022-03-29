package ie.ul.ethics.scieng.exporter.services;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.search.ApplicationSpecification;
import ie.ul.ethics.scieng.applications.search.SubmittedApplicationSpecification;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.common.search.SearchParser;
import ie.ul.ethics.scieng.exporter.ExportedApplication;
import ie.ul.ethics.scieng.exporter.pdf.PDFExportedApplication;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Create a PDF Exporter Service
     * @param applicationService the service to retrieve applications with
     * @param fileService the service to load files
     */
    @Autowired
    public PDFExporterService(ApplicationService applicationService, FileService fileService) {
        this.applicationService = applicationService;
        this.fileService = fileService;
    }

    /**
     * The internal method to export the application
     * @param application the application to export
     * @return the exported application
     * @throws IOException if an error occurs
     */
    private ExportedApplication export(Application application) throws IOException {
        if (application.getStatus() == ApplicationStatus.DRAFT) {
            OutputStream outputStream = null; // TODO parse application into a PDF output stream here
            List<AttachedFile> attachedFiles = application.getAttachedFiles();
            List<File> files = new ArrayList<>();

            if (attachedFiles != null) {
                for (AttachedFile attachedFile : attachedFiles) {
                    try {
                        Resource resource = fileService.loadFile(attachedFile.getFilename(), attachedFile.getDirectory(), attachedFile.getUsername());

                        if (resource != null)
                            files.add(resource.getFile());
                    } catch (FileException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            return new PDFExportedApplication(outputStream, files);
        } else {
            return null;
        }
    }

    /**
     * Export a single application with the given id (only applications that don't have DRAFT status)
     *
     * @param id the ethics application ID
     * @return the exported application
     * @throws IOException if an error occurs
     */
    @Override
    public ExportedApplication exportApplication(String id) throws IOException {
        Application application = this.applicationService.getApplication(id);

        if (application == null) {
            return null;
        } else {
            return export(application);
        }
    }

    /**
     * Export all non-DRAFT applications in the system
     *
     * @return the list of exported applications
     * @throws IOException if an error occurs
     */
    @Override
    public List<ExportedApplication> exportApplications() throws IOException {
        return exportApplications(applicationService.getApplications());
    }

    /**
     * Export the applications in the given list
     * @param applications the list of applications to export
     * @return the list of exported applications
     * @throws IOException if an error occurs
     */
    private List<ExportedApplication> exportApplications(List<Application> applications) throws IOException {
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
     * @throws IOException if an error occurs
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ExportedApplication> exportApplications(LocalDate start, LocalDate end) throws IOException {
        Class<? extends ApplicationSpecification> specificationClass = SubmittedApplicationSpecification.class;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = start.format(formatter);
        String endDate = end.format(formatter);
        String query = String.format("submittedTime>%s,submittedTime<%s", startDate, endDate);

        Specification<Application> specification = new SearchParser<>(specificationClass).parse(query, ApplicationSpecification.OPERATION_PATTERN, false);

        return exportApplications(applicationService.search(specification));
    }
}
