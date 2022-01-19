package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.repositories.ApplicationRepository;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
     * The user service for checking user's permissions on application services
     */
    private final UserService userService;

    /**
     * Create an ApplicationServiceImpl
     * @param templateRepository the template repository for saving application templates
     * @param applicationRepository the repository for saving and loading applications
     * @param templateLoader the loader for application template loading
     * @param userService the user service for checking user's permissions on application services
     */
    @Autowired
    public ApplicationServiceImpl(ApplicationTemplateRepository templateRepository, ApplicationRepository applicationRepository,
                                  ApplicationTemplateLoader templateLoader, UserService userService) {
        this.templateRepository = templateRepository;
        this.applicationRepository = applicationRepository;
        this.templateLoader = templateLoader;
        this.userService = userService;
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
}