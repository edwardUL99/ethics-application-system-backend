package ie.ul.edward.ethics.applications.services;

import ie.ul.edward.ethics.applications.models.applications.Application;
import ie.ul.edward.ethics.applications.models.applications.ApplicationStatus;
import ie.ul.edward.ethics.applications.models.applications.DraftApplication;
import ie.ul.edward.ethics.applications.repositories.ApplicationRepository;
import ie.ul.edward.ethics.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.edward.ethics.users.models.User;
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
     * Create an ApplicationServiceImpl
     * @param templateRepository the template repository for saving application templates
     * @param applicationRepository the repository for saving and loading applications
     */
    @Autowired
    public ApplicationServiceImpl(ApplicationTemplateRepository templateRepository, ApplicationRepository applicationRepository) {
        this.templateRepository = templateRepository;
        this.applicationRepository = applicationRepository;
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
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application createApplication(Application application) {
        application.setLastUpdated(LocalDateTime.now());
        applicationRepository.save(application);

        return application;
    }

    /**
     * Does some required processing on a draft application and then passes it to {@link #createApplication(Application)}
     *
     * @param draftApplication the application to create
     * @param update           true if it's an update, false if new
     * @return the created application
     * @throws IllegalStateException if draft application's ID is null and update is true
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application createDraftApplication(DraftApplication draftApplication, boolean update) {
        if (update && draftApplication.getId() == null)
            throw new IllegalStateException("You cannot update a DraftApplication that has no ID");

        if (!update)
            templateRepository.save(draftApplication.getApplicationTemplate());

        createApplication(draftApplication);

        return draftApplication;
    }
}
