package ie.ul.edward.ethics.applications.services;

import ie.ul.edward.ethics.applications.models.applications.Application;
import ie.ul.edward.ethics.applications.models.applications.ApplicationStatus;
import ie.ul.edward.ethics.applications.models.applications.DraftApplication;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.users.models.User;

import java.util.List;

/*
TODO implement and test. May need other functions for application processing but they can be done/looked at later
 */

/**
 * This interface represents a service for interacting with applications
 */
public interface ApplicationService {
    /**
     * Get the application that matches the given ID
     * @param id the id of the application
     * @return the application if found, null if not
     */
    Application getApplication(Long id);

    /**
     * Get the list of applications created by the given user
     * @param user the user to search for applications by
     * @return the list of applications
     */
    List<Application> getUserApplications(User user);

    /**
     * Retrieve all applications with the provided status
     * @param status the status of the applications to find
     * @return the list of found applications
     */
    List<Application> getApplicationsWithStatus(ApplicationStatus status);

    /**
     * Create/update the application
     * @param application the application to save
     * @return the saved application
     */
    Application createApplication(Application application);

    /**
     * Does some required processing on a draft application and then passes it to {@link #createApplication(Application)}
     * @param draftApplication the application to create
     * @param update true if it's an update, false if new
     * @return the created application
     * @throws IllegalStateException if draft application's ID is null and update is true
     */
    Application createDraftApplication(DraftApplication draftApplication, boolean update);

    /**
     * Load and return the application template with the given ID
     * @param id the id of the saved template
     * @return the saved template or null if not found
     */
    ApplicationTemplate getApplicationTemplate(Long id);
}
