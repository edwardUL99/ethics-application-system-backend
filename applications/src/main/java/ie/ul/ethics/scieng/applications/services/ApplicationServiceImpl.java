package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.email.ApplicationsEmailService;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationComments;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AssignedCommitteeMember;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.ReferredApplication;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.repositories.AnswerRequestRepository;
import ie.ul.ethics.scieng.applications.repositories.ApplicationRepository;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.services.FileService;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@Log4j2
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
     * The service for interacting with files
     */
    private final FileService fileService;
    /**
     * The entity manager for interacting with entity API
     */
    @PersistenceContext
    private EntityManager entityManager;
    /**
     * The repository for storing answer requests
     */
    private final AnswerRequestRepository requestRepository;

    /**
     * Create an ApplicationServiceImpl
     * @param templateRepository the template repository for saving application templates
     * @param applicationRepository the repository for saving and loading applications
     * @param templateLoader the loader for application template loading
     * @param emailService the service for sending applications notifications
     * @param fileService the service for interacting with files
     * @param requestRepository the repository for storing answer requests
     */
    @Autowired
    public ApplicationServiceImpl(ApplicationTemplateRepository templateRepository, ApplicationRepository applicationRepository,
                                  ApplicationTemplateLoader templateLoader, @Qualifier("applicationsEmail") ApplicationsEmailService emailService,
                                  FileService fileService, AnswerRequestRepository requestRepository) {
        this.templateRepository = templateRepository;
        this.applicationRepository = applicationRepository;
        this.templateLoader = templateLoader;
        this.emailService = emailService;
        this.fileService = fileService;
        this.requestRepository = requestRepository;
    }

    /**
     * Get the application that matches the given ID
     *
     * @param id the id of the application
     * @return the application if found, null if not
     */
    @Override
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
    @Transactional
    public Application createApplication(Application application, boolean update) {
        if (update && application.getId() == null)
            throw new ApplicationException("You cannot update an Application that has no ID");

        ApplicationTemplate template = application.getApplicationTemplate();
        application.setApplicationTemplate(templateRepository.save(template));
        application.setLastUpdated(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    /**
     * Load and return the application template with the given ID
     *
     * @param id the id of the saved template
     * @return the saved template or null if not found
     */
    @Override
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
        Map<String, Answer> mapped = new HashMap<>();
        answers.forEach((k, v) ->
            mapped.put(k, new Answer(null, v.getComponentId(), v.getValue(), v.getValueType(), v.getUser()))); // FIXME this may not be ideal

        return mapped;
    }

    /**
     * Get the attached files from the application to attach to a new application
     * @param application the application to map attachments from
     * @return the list of attachments
     */
    private List<AttachedFile> getAttachedFiles(Application application) {
        return application.getAttachedFiles()
                .stream()
                .map(AttachedFile::copy)
                .collect(Collectors.toList());
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
    @Transactional
    public Application submitApplication(Application application) throws InvalidStatusException {
        Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.DRAFT, ApplicationStatus.REFERRED);
        ApplicationStatus status = application.getStatus();
        Map<String, Answer> answers = mapAnswers(application.getAnswers());

        if (status == null || !permissible.contains(status))
            throw new InvalidStatusException("The status of an application being submitted must belong to the set: " + permissible);

        Application submittedApplication = new SubmittedApplication(null, application.getApplicationId(), application.getUser(),
                ApplicationStatus.SUBMITTED, application.getApplicationTemplate(), answers,
                getAttachedFiles(application), new ArrayList<>(), new ArrayList<>(), null);

        if (status == ApplicationStatus.REFERRED) {
            application.getAssignedCommitteeMembers().forEach(u -> submittedApplication.assignCommitteeMember(u.getUser()));
            submittedApplication.setStatus(ApplicationStatus.RESUBMITTED);
            submittedApplication.assignCommitteeMembersToPrevious();
            submittedApplication.setComments(mapComments(application.getComments()));
        }

        submittedApplication.setLastUpdated(LocalDateTime.now());
        submittedApplication.setSubmittedTime(LocalDateTime.now());
        deleteApplicationInstance(application); // delete the draft application with the same applicationId and replace it with the submitted application
        entityManager.flush();

        return applicationRepository.save(submittedApplication);
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
    public Application assignCommitteeMembers(Application application, List<User> committeeMembers) throws ApplicationException {
        if (!List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW).contains(application.getStatus()))
            throw new InvalidStatusException("The application is in an invalid status for assigning committee members");

        for (User user : committeeMembers) {
            try {
                application.assignCommitteeMember(user);
            } catch (ApplicationException ex) {
                throw new ApplicationException(CANT_REVIEW, ex);
            }
        }

        return this.createApplication(application, true);
    }

    /**
     * Unassign the user from the committee member
     *
     * @param application the application to remove the member from
     * @param username    the username of the committee member to remove
     * @return the modified application
     * @throws ApplicationException if the status is incorrect or no committee member with username is assigned
     */
    @Override
    public Application unassignCommitteeMember(Application application, String username) throws ApplicationException {
        if (!List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW).contains(application.getStatus()))
            throw new InvalidStatusException("The application is in an invalid status for assigning committee members");

        List<AssignedCommitteeMember> assigned = application.getAssignedCommitteeMembers()
                .stream()
                .filter(a -> !a.getUser().getUsername().equals(username))
                .collect(Collectors.toList());

        application.setAssignedCommitteeMembers(assigned);

        return createApplication(application, true);
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
    public Application acceptResubmitted(Application application, List<User> committeeMembers) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.RESUBMITTED)
            throw new InvalidStatusException("The application status must be " + ApplicationStatus.RESUBMITTED + " to use this method");

        application.setStatus(ApplicationStatus.REVIEW);
        committeeMembers.forEach(application::assignCommitteeMember);
        application.clearPreviousCommitteeMembers();

        application.setLastUpdated(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    /**
     * Mark an application as being in review and no longer submitted.
     *
     * @param application  the application to put into review
     * @return the application instance after it being updated
     * @throws InvalidStatusException if the application is not in the submitted state and finishReview is false
     */
    @Override
    public Application reviewApplication(Application application, boolean finishReview) throws InvalidStatusException {
        ApplicationStatus status = application.getStatus();

        if (!finishReview && status != ApplicationStatus.SUBMITTED && status != ApplicationStatus.REVIEWED)
            throw new InvalidStatusException("You can only set an application to " + ApplicationStatus.REVIEW + " if it is in the "
                + ApplicationStatus.SUBMITTED + " or " + ApplicationStatus.REVIEWED + " status");
        else if (finishReview && status != ApplicationStatus.REVIEW)
            throw new InvalidStatusException("To finish a review, the application must be in the status " + ApplicationStatus.REVIEW);

        application.setStatus((finishReview) ? ApplicationStatus.REVIEWED:ApplicationStatus.REVIEW);
        application.getAssignedCommitteeMembers().forEach(a -> {
            if (finishReview)
                a.setFinishReview(true);
        });

        return createApplication(application, true);
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
    public Application markMemberReviewComplete(Application application, String member) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.REVIEW)
            throw new InvalidStatusException("You can only mark a committee member as having finished their review on an application in " +
                    "the status " + ApplicationStatus.REVIEW);

        application.getAssignedCommitteeMembers()
                .stream()
                .filter(u -> u.getUser().getUsername().equals(member))
                .filter(u -> u.getUser().getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
                .findFirst().ifPresent(assigned -> assigned.setFinishReview(true));

        return this.createApplication(application, true);
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
    public Application approveApplication(Application application, boolean approve, Comment finalComment) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.REVIEWED)
            throw new InvalidStatusException("To approve/reject an Application, its status must be " + ApplicationStatus.REVIEWED);

        ApplicationStatus target = (approve) ? ApplicationStatus.APPROVED:ApplicationStatus.REJECTED;
        application.setStatus(target);
        application.setFinalComment(finalComment);
        application.setApprovalTime((approve) ? LocalDateTime.now():null);
        application = createApplication(application, true);

        emailService.sendApplicationApprovalEmail(application);

        return application;
    }

    /**
     * Map comments to a way where they're able to be saved without detached instance exceptions
     * @param comments the comments to map
     */
    private Map<String, ApplicationComments> mapComments(Map<String, ApplicationComments> comments) {
        Map<String, ApplicationComments> mapped = new HashMap<>();
        comments.forEach((k, v) -> {
            List<Comment> commentList = new ArrayList<>();
            String componentId = v.getComponentId();
            ApplicationComments comments1 = new ApplicationComments(null, componentId, commentList);
            comments1.setComponentId(componentId);
            v.getComments().forEach(c -> commentList.add(c.copy()));
            mapped.put(componentId, comments1);
        });

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
    @Transactional
    public Application referApplication(Application application, List<String> editableFields, User referrer) throws ApplicationException {
        if (application.getStatus() != ApplicationStatus.REVIEWED)
            throw new InvalidStatusException("To refer an application, its status must be " + ApplicationStatus.REVIEWED);

        Map<String, ApplicationComments> comments = mapComments(application.getComments());
        Comment finalComment = application.getFinalComment();

        if (finalComment != null)
            finalComment.setId(null);

        Map<String, Answer> answers = mapAnswers(application.getAnswers());

        List<AssignedCommitteeMember> assigned = application.getAssignedCommitteeMembers()
                .stream()
                .map(a -> new AssignedCommitteeMember(null, a.getApplicationId(), a.getUser(), a.isFinishReview()))
                .collect(Collectors.toList());

        Application referredApplication =
                new ReferredApplication(null, application.getApplicationId(), application.getUser(), application.getApplicationTemplate(),
                        answers, getAttachedFiles(application), new ArrayList<>(comments.values()), assigned, finalComment,
                        editableFields, referrer);

        referredApplication.setLastUpdated(LocalDateTime.now());
        referredApplication.setSubmittedTime(application.getSubmittedTime());

        deleteApplicationInstance(application);
        entityManager.flush();
        referredApplication = applicationRepository.save(referredApplication);

        emailService.sendApplicationReferredEmail(referredApplication, referrer);

        return referredApplication;
    }

    /**
     * Deletes the application instance from the repository if it is to be replaced by another application instance,
     * i.e. save a submitted application after deleting draft
     * @param application the application to delete
     */
    private void deleteApplicationInstance(Application application) {
        this.requestRepository.deleteByApplication_id(application.getId());
        this.applicationRepository.delete(application);
    }

    /**
     * Deletes the provided application
     *
     * @param application the application to delete
     */
    @Override
    @Transactional
    public void deleteApplication(Application application) {
        deleteApplicationInstance(application);
        this.templateRepository.delete(application.getApplicationTemplate());

        for (AttachedFile attachedFile : application.getAttachedFiles()) {
            try {
                this.fileService.deleteFile(attachedFile.getFilename(), attachedFile.getDirectory(), attachedFile.getUsername());
            } catch (FileException ex) {
                log.error(ex);
            }
        }
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

    /**
     * Patch the answers of the application. If an answer with the same component ID exists, it is replaced, else it is
     * added
     *
     * @param application the application to patch
     * @param answers     the answers to patch
     * @return the patched application
     */
    @Override
    public Application patchAnswers(Application application, Map<String, Answer> answers) {
        Map<String, Answer> applicationAnswers = application.getAnswers();

        for (Map.Entry<String, Answer> e : answers.entrySet()) {
            String key = e.getKey();

            if (!applicationAnswers.containsKey(key)) {
                applicationAnswers.put(key, e.getValue());
            } else {
                Answer saved = applicationAnswers.get(key);
                Answer newAnswer = e.getValue();
                newAnswer.setId(saved.getId());
                applicationAnswers.put(key, newAnswer);
            }
        }

        return createApplication(application, true);
    }

    /**
     * Do the patch on the comments
     * @param applicationComments the comments omn the application
     * @param componentId the ID of the component the comment is on
     * @param comments the comments being updated
     * @param updated the modified comment
     * @param delete the comment to delete
     * @return true if patched, false if not
     */
    private boolean doPatch(Map<String, ApplicationComments> applicationComments, String componentId,
                            ApplicationComments comments, Comment updated, boolean delete) {
        List<Comment> list = comments.getComments();
        Comment found = null;
        Long updateId = updated.getId();

        if (updateId == null)
            throw new ApplicationException("Cannot update a non-saved comment");

        for (int i = 0; i < list.size() && found == null; i++) {
            Comment comment = list.get(i);
            Long id = comment.getId();

            if (id == null)
                throw new ApplicationException("Cannot update a non-saved comment");
            else if (id.equals(updateId))
                found = list.get(i);
        }

        if (found != null) {
            if (delete) {
                list.remove(found);

                if (list.size() == 0)
                    applicationComments.remove(componentId);
            } else {
                found.merge(updated);
            }

            return true;
        }

        return false;
    }

    /**
     * Patch the comments on the application. Only updates from top-level comments and not sub-comments
     *
     * @param application the application to patch the comment on
     * @param updated     the comment to update
     * @param delete      true to delete the comment, false to update
     * @return the patched application
     */
    @Override
    @Transactional
    public Application patchComment(Application application, Comment updated, boolean delete) {
        if (updated.getParent() != null)
            throw new ApplicationException("The comment must be a top-level comment and not a sub-comment. To edit a sub-comment, " +
                    "update the sub-comment and then send the parent comment with the edited sub-comment in the request");

        String componentId = updated.getComponentId();
        Map<String, ApplicationComments> applicationComments = application.getComments();
        ApplicationComments comments;

        if (componentId != null && (comments = applicationComments.get(componentId)) != null) {
            if (doPatch(applicationComments, componentId, comments, updated, delete))
                application = createApplication(application, true);

            return application;
        } else {
            return null;
        }
    }
}
