package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.repositories.ApplicationRepository;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * This class is the implementation of the ApplicationService
 */
@Service
@CacheConfig(cacheNames = "applications")
public class ApplicationServiceImpl implements ApplicationService {
    /**
     * The template repository for saving application templates
     */
    private final ApplicationTemplateRepository templateRepository;
    /**
     * The repository for saving and loading applications
     */
    private final ApplicationRepository applicationRepository;
    /**
     * The loader for loading application templates
     */
    private final ApplicationTemplateLoader templateLoader;

    /**
     * Create an ApplicationServiceImpl
     * @param templateRepository the template repository for saving application templates
     * @param applicationRepository the repository for saving and loading applications
     * @param templateLoader the loader for application template loading
     */
    @Autowired
    public ApplicationServiceImpl(ApplicationTemplateRepository templateRepository, ApplicationRepository applicationRepository,
                                  ApplicationTemplateLoader templateLoader) {
        this.templateRepository = templateRepository;
        this.applicationRepository = applicationRepository;
        this.templateLoader = templateLoader;
    }

    /**
     * Get the application that matches the given ID
     *
     * @param id the id of the application
     * @return the application if found, null if not
     */
    @Override
    @Cacheable(value = "application")
    public Application getApplication(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    /**
     * Retrieve the application by the applicationId attribute
     *
     * @param applicationId the applicationId to retrieve by
     * @return the application if found, null if not
     */
    @Override
    @Cacheable(value = "application")
    public Application getApplication(String applicationId) {
        return applicationRepository.findByApplicationId(applicationId).orElse(null);
    }

    /**
     * Get the list of applications created by the given user
     *
     * @param user the user to search for applications by
     * @return the list of applications
     */
    @Override
    @Cacheable(value = "user_applications")
    public List<Application> getUserApplications(User user) {
        return applicationRepository.findByUser(user);
    }

    /**
     * Retrieve all applications with the provided status
     *
     * @param status the status of the applications to find
     * @return the list of found applications
     */
    @Override
    @Cacheable(value = "status_applications")
    public List<Application> getApplicationsWithStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    /**
     * Create/update the application
     *
     * @param application the application to save
     * @return the saved application
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true),
            @CacheEvict(value = "template", allEntries = true)
    })
    public Application createApplication(Application application, boolean update) {
        if (update && application.getId() == null)
            throw new ApplicationException("You cannot update a DraftApplication that has no ID");

        if (!update)
            templateRepository.save(application.getApplicationTemplate());

        application.setLastUpdated(LocalDateTime.now());
        applicationRepository.save(application);

        return application;
    }

    /**
     * Load and return the application template with the given ID
     *
     * @param id the id of the saved template
     * @return the saved template or null if not found
     */
    @Override
    @Cacheable(value = "template")
    public ApplicationTemplate getApplicationTemplate(Long id) {
        return templateRepository.findById(id).orElse(null);
    }

    /**
     * Get all the application templates loaded into the system
     *
     * @return array of loaded templates
     */
    @Override
    public ApplicationTemplate[] getApplicationTemplates() {
        return templateLoader.loadTemplates();
    }

    /**
     * Submit an application from the applicant to the committee and convert the application to a submitted state.
     * The draft instance of the application will be removed and replaced with the submitted instance. The database IDs
     * will differ but the applicationId field will remain the same.
     *
     * @param application the application to submit
     * @return the submitted application
     * @throws ApplicationException if the application is not in a draft or referred state
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application submitApplication(Application application) throws ApplicationException {
        Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.DRAFT, ApplicationStatus.REFERRED);
        ApplicationStatus status = application.getStatus();

        if (status == null || !permissible.contains(status))
            throw new ApplicationException("The status of an application being submitted must belong to the set: " + permissible);

        SubmittedApplication submittedApplication = new SubmittedApplication();
        submittedApplication.setStatus(ApplicationStatus.SUBMITTED);
        submittedApplication.setApplicationId(application.getApplicationId());
        submittedApplication.setUser(application.getUser());
        submittedApplication.setApplicationTemplate(application.getApplicationTemplate());
        submittedApplication.setAnswers(application.getAnswers());
        submittedApplication.setLastUpdated(LocalDateTime.now());

        applicationRepository.delete(application);
        applicationRepository.save(submittedApplication);

        return submittedApplication;
    }
}
