package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.models.User;

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
     * @param update true to update, false to create
     * @return the saved application
     */
    Application createApplication(Application application, boolean update);

    /**
     * Load and return the application template with the given ID
     * @param id the id of the saved template
     * @return the saved template or null if not found
     */
    ApplicationTemplate getApplicationTemplate(Long id);

    /**
     * Get all the application templates loaded into the system
     * @return array of loaded templates
     */
    ApplicationTemplate[] getApplicationTemplates();
}
