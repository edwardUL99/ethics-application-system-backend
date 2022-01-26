package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
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
     * Retrieve the application by the applicationId attribute
     * @param applicationId the applicationId to retrieve by
     * @return the application if found, null if not
     */
    Application getApplication(String applicationId);

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
     * Get all the applications assigned to the user
     * @param assigned the user that is assigned to the application
     * @return the list of assigned applications
     * @throws ApplicationException if they do not have permissions to be assigned to applications
     */
    List<Application> getAssignedApplications(User assigned);

    /**
     * Get all the applications that can be viewed by the provided user
     * @param user the user that wishes to retrieve the applications
     * @return the list of applications that the user can view
     */
    List<Application> getViewableApplications(User user);

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

    /**
     * Submit an application from the applicant to the committee and convert the application to a submitted state.
     * The draft instance of the application will be removed and replaced with the submitted instance. The database IDs
     * will differ but the applicationId field will remain the same.
     *
     * If the application being submitted has been referred, the referring user will be added to the list of assigned committee
     * members in the submitted application
     * @param application the application to submit
     * @return the submitted application
     * @throws InvalidStatusException if the application is not in a draft or referred state
     */
    Application submitApplication(Application application) throws InvalidStatusException;

    /**
     * Accept an application that has been re-submitted and assign the list of committee members to the application.
     * After this method is called, the application will be "reset" to the submitted state with the assigned committee members
     * @param application the application to accept
     * @param committeeMembers the list of committee members to assign
     * @return the updated application
     * @throws InvalidStatusException if the application status is not re-submitted
     */
    Application acceptResubmitted(Application application, List<User> committeeMembers) throws InvalidStatusException;

    /**
     * Mark an application as being in review and no longer submitted.
     * @param application the application to put into review
     * @param finishReview if true, the application is marked as reviewed
     * @return the application instance after it being updated
     * @throws InvalidStatusException if the application is not in the submitted state and finishReview is false. If
     * finishReview is true and the application is not in a review state, this exception will be thrown
     */
    Application reviewApplication(Application application, boolean finishReview) throws InvalidStatusException;

    /**
     * Mark the approval status on the application. The only user's that should have access to this method are those
     * that have the APPROVE_APPLICATION permission
     * @param application the application to set the approval status
     * @param approve true to approve the application, false to reject it // TODO after prototype, implement approve with minor/major clarifications
     * @param finalComment the final comment to leave on the application
     * @return the application after being updated
     * @throws InvalidStatusException if the application is not in a reviewed state
     */
    Application approveApplication(Application application, boolean approve, Comment finalComment) throws InvalidStatusException;

    /**
     * Refer the application to the user that created the application. This should result in the submitted version of the
     * application being removed from the system and replaced with the referred application
     * @param application the application that is to be referred
     * @param editableFields the list of field IDs that can be edited
     * @param referrer the user that is referring the application to the user
     * @return the referred application instance
     * @throws ApplicationException if the application is not in a reviewed state or the referrer does not have the REFER_APPLICATIONS permission
     */
    Application referApplication(Application application, List<String> editableFields, User referrer) throws ApplicationException;
}
