package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.email.ApplicationsEmailService;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.ReferredApplication;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.repositories.ApplicationRepository;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
     * The email service to use for sending notification e-mails
     */
    private final ApplicationsEmailService emailService;

    /**
     * Create an ApplicationServiceImpl
     * @param templateRepository the template repository for saving application templates
     * @param applicationRepository the repository for saving and loading applications
     * @param templateLoader the loader for application template loading
     * @param emailService the service for sending applications notifications
     */
    @Autowired
    public ApplicationServiceImpl(ApplicationTemplateRepository templateRepository, ApplicationRepository applicationRepository,
                                  ApplicationTemplateLoader templateLoader, @Qualifier("applicationsEmail") ApplicationsEmailService emailService) {
        this.templateRepository = templateRepository;
        this.applicationRepository = applicationRepository;
        this.templateLoader = templateLoader;
        this.emailService = emailService;
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
     * Get all the applications assigned to the user
     *
     * @param assigned the user that is assigned to the application
     * @return the list of assigned applications
     * @throws ApplicationException if they do not have permissions to be assigned to applications
     */
    @Override
    public List<Application> getAssignedApplications(User assigned) {
        if (!assigned.getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
            throw new ApplicationException("The user must have the REVIEW_APPLICATIONS permission");

        List<Application> applications = new ArrayList<>();
        applicationRepository.findAll().forEach(applications::add);
        String assignedUsername = assigned.getUsername();

        return applications.stream()
                .filter(a -> a instanceof SubmittedApplication)
                .map(a -> (SubmittedApplication)a)
                .filter(a -> a.getAssignedCommitteeMembers()
                        .stream()
                        .map(m -> m.getUser().getUsername())
                        .anyMatch(u -> u.equals(assignedUsername)))
                .collect(Collectors.toList());
    }

    /**
     * Get all the applications that can be viewed by the provided user
     *
     * @param user the user that wishes to retrieve the applications
     * @return the list of applications that the user can view
     */
    @Override
    public List<Application> getViewableApplications(User user) {
        List<Application> all = new ArrayList<>();
        applicationRepository.findAll().forEach(all::add);

        return all.stream()
                .filter(a -> a.canBeViewedBy(user))
                .collect(Collectors.toList());
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
            throw new ApplicationException("You cannot update an Application that has no ID");

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
        ApplicationTemplate template = templateRepository.findById(id).orElse(null);

        if (template != null)
            template.sort();

        return template;
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
     * Prepare the answers for saving on a new application instance. Required to fix an issue with detached instances exceptions
     * @param answers the answers to map
     * @return tha map of answers
     */
    private Map<String, Answer> mapAnswers(Map<String, Answer> answers) {
        Map<String, Answer> mapped = new HashMap<>(answers);
        mapped.forEach((k, v) -> v.setId(null)); // FIXME this may not be ideal

        return mapped;
    }

    /**
     * Submit an application from the applicant to the committee and convert the application to a submitted state.
     * The draft instance of the application will be removed and replaced with the submitted instance. The database IDs
     * will differ but the applicationId field will remain the same.
     *
     * @param application the application to submit
     * @return the submitted application
     * @throws InvalidStatusException if the application is not in a draft or referred state
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application submitApplication(Application application) throws InvalidStatusException {
        Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.DRAFT, ApplicationStatus.REFERRED);
        ApplicationStatus status = application.getStatus();
        Map<String, Answer> answers = mapAnswers(application.getAnswers());

        if (status == null || !permissible.contains(status))
            throw new InvalidStatusException("The status of an application being submitted must belong to the set: " + permissible);

        SubmittedApplication submittedApplication = new SubmittedApplication(null, application.getApplicationId(), application.getUser(),
                ApplicationStatus.SUBMITTED, application.getApplicationTemplate(), answers,
                new ArrayList<>(), new ArrayList<>(), null);

        if (status == ApplicationStatus.REFERRED) {
            ReferredApplication referred = (ReferredApplication) application;
            submittedApplication.assignCommitteeMember(referred.getReferredBy());
            referred.getAssignedCommitteeMembers().forEach(u -> referred.assignCommitteeMember(u.getUser()));
            submittedApplication.setStatus(ApplicationStatus.RESUBMITTED);
            submittedApplication.assignCommitteeMembersToPrevious();
        }

        applicationRepository.delete(application); // delete the draft application with the same applicationId and replace it with the submitted application

        submittedApplication.setLastUpdated(LocalDateTime.now());
        submittedApplication.setSubmittedTime(LocalDateTime.now());
        applicationRepository.save(submittedApplication);

        return submittedApplication;
    }

    /**
     * Assign the list of committee members to the application
     *
     * @param application      the application to assign the committee members to
     * @param committeeMembers the list of committee members to assign
     * @return the application after updating it
     * @throws ApplicationException if the status is incorrect or an exception with message CANT_REVIEW if the user is not a committee member
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application assignCommitteeMembers(Application application, List<User> committeeMembers) throws ApplicationException {
        if (!List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW).contains(application.getStatus()))
            throw new InvalidStatusException("The application is in an invalid status for assigning committee members");

        SubmittedApplication submitted = (SubmittedApplication) application;

        for (User user : committeeMembers) {
            try {
                submitted.assignCommitteeMember(user);
            } catch (ApplicationException ex) {
                throw new ApplicationException(CANT_REVIEW, ex);
            }
        }

        this.createApplication(submitted, true);

        return submitted;
    }

    /**
     * Accept an application that has been re-submitted and assign the list of committee members to the application.
     * After this method is called, the application will be "reset" to the submitted state with the assigned committee members
     *
     * @param application      the application to accept
     * @param committeeMembers the list of committee members to assign
     * @return the updated application
     * @throws InvalidStatusException if the application status is not re-submitted
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application acceptResubmitted(Application application, List<User> committeeMembers) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.RESUBMITTED)
            throw new InvalidStatusException("The application status must be " + ApplicationStatus.RESUBMITTED + " to use this method");

        SubmittedApplication submitted = (SubmittedApplication) application;
        application.setStatus(ApplicationStatus.REVIEW);
        committeeMembers.forEach(submitted::assignCommitteeMember);
        submitted.clearPreviousCommitteeMembers();

        submitted.setLastUpdated(LocalDateTime.now());

        applicationRepository.save(submitted);

        return submitted;
    }

    /**
     * Mark an application as being in review and no longer submitted.
     *
     * @param application  the application to put into review
     * @return the application instance after it being updated
     * @throws InvalidStatusException if the application is not in the submitted state and finishReview is false
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application reviewApplication(Application application, boolean finishReview) throws InvalidStatusException {
        ApplicationStatus status = application.getStatus();

        if (!finishReview && status != ApplicationStatus.SUBMITTED)
            throw new InvalidStatusException("You can only set an application to " + ApplicationStatus.REVIEW + " if it is in the "
                + ApplicationStatus.SUBMITTED + " status");
        else if (finishReview && status != ApplicationStatus.REVIEW)
            throw new InvalidStatusException("To finish a review, the application must be in the status " + ApplicationStatus.REVIEW);

        application.setStatus((finishReview) ? ApplicationStatus.REVIEWED:ApplicationStatus.REVIEW);
        createApplication(application, true);

        return application;
    }

    /**
     * Mark the committee member as their review being finished if they are assigned to the application
     *
     * @param application the application being modified
     * @param member      the username of the member that is finished their review
     * @return the modified application
     * @throws InvalidStatusException if the application is not in review
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application markMemberReviewComplete(Application application, String member) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.SUBMITTED)
            throw new InvalidStatusException("You can only mark a committee member as having finished their review on an application in " +
                    "the status " + ApplicationStatus.SUBMITTED);

        SubmittedApplication submitted = (SubmittedApplication) application;
        submitted.getAssignedCommitteeMembers()
                .stream()
                .filter(u -> u.getUser().getUsername().equals(member))
                .filter(u -> u.getUser().getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
                .findFirst().ifPresent(assigned -> assigned.setFinishReview(true));

        this.createApplication(submitted, true);

        return submitted;
    }

    /**
     * Mark the approval status on the application. The only user's that should have access to this method are those
     * that have the APPROVE_APPLICATION permission
     *
     * @param application the application to set the approval status
     * @param approve     true to approve the application, false to reject it // TODO after prototype, implement approve with minor/major clarifications
     * @param finalComment the final comment to leave on the application
     * @return the application after being updated
     * @throws InvalidStatusException if the application is not in a reviewed state
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application approveApplication(Application application, boolean approve, Comment finalComment) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.REVIEWED)
            throw new InvalidStatusException("To approve/reject an Application, its status must be " + ApplicationStatus.REVIEWED);

        ApplicationStatus target = (approve) ? ApplicationStatus.APPROVED:ApplicationStatus.REJECTED;
        application.setStatus(target);
        SubmittedApplication submittedApplication = (SubmittedApplication) application;
        submittedApplication.setFinalComment(finalComment);
        submittedApplication.setApprovalTime((approve) ? LocalDateTime.now():null);
        createApplication(application, true);

        // TODO send notification of approval status by email

        return application;
    }

    /**
     * Map comments to a way where they're able to be saved without detached instance exceptions
     * @param comments the comments to map
     */
    private Map<String, Comment> mapComments(Map<String, Comment> comments) {
        Map<String, Comment> mapped = new HashMap<>(comments);
        mapped.forEach((k, v) -> v.setId(null));

        return mapped;
    }

    /**
     * Refer the application to the user that created the application. This should result in the submitted version of the
     * application being removed from the system and replaced with the referred application
     *
     * @param application the application that is to be referred
     * @param editableFields the list of field IDs that can be edited
     * @param referrer    the user that is referring the application to the user
     * @return the referred application instance
     * @throws ApplicationException if the application is not in a reviewed state or the referrer does not have the REFER_APPLICATIONS permission
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public Application referApplication(Application application, List<String> editableFields, User referrer) throws ApplicationException {
        if (application.getStatus() != ApplicationStatus.REVIEWED)
            throw new InvalidStatusException("To refer an application, its status must be " + ApplicationStatus.REVIEWED);

        SubmittedApplication submitted = (SubmittedApplication) application;

        Map<String, Comment> comments = mapComments(submitted.getComments());
        Comment finalComment = submitted.getFinalComment();

        if (finalComment != null)
            finalComment.setId(null);

        Map<String, Answer> answers = mapAnswers(submitted.getAnswers());

        ReferredApplication referredApplication =
                new ReferredApplication(null, application.getApplicationId(), application.getUser(), application.getApplicationTemplate(),
                        answers, new ArrayList<>(comments.values()), submitted.getAssignedCommitteeMembers(), finalComment,
                        editableFields, referrer);

        referredApplication.setLastUpdated(LocalDateTime.now());
        referredApplication.setSubmittedTime(submitted.getSubmittedTime());

        applicationRepository.delete(application);
        applicationRepository.save(referredApplication);

        emailService.sendApplicationReferredEmail(application, referrer);

        return referredApplication;
    }

    /**
     * Deletes the provided application
     *
     * @param application the application to delete
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "application", allEntries = true),
            @CacheEvict(value = "user_applications", allEntries = true),
            @CacheEvict(value = "status_applications", allEntries = true)
    })
    public void deleteApplication(Application application) {
        this.applicationRepository.delete(application);
        this.templateRepository.delete(application.getApplicationTemplate());
    }

    /**
     * Search for applications matching the given specification
     *
     * @param specification the specification to search with
     * @return the list of found applications
     */
    @Override
    public List<Application> search(Specification<Application> specification) {
        return this.applicationRepository.findAll(specification);
    }
}
