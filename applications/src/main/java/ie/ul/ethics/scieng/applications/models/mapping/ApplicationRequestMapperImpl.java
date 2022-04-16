package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.exceptions.MappingException;
import ie.ul.ethics.scieng.applications.models.ApproveApplicationRequest;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.ReferApplicationRequest;
import ie.ul.ethics.scieng.applications.models.SubmitApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.ReviewSubmittedApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This provides the default implementation for the mapper
 */
@Component
@Log4j2
public class ApplicationRequestMapperImpl implements ApplicationRequestMapper {
    /**
     * The user service to help with mapping
     */
    private final UserService userService;
    /**
     * The application service to help with mapping
     */
    private final ApplicationService applicationService;

    /**
     * Create an ApplicationRequestMapperImpl
     * @param userService the user service to help with mapping
     * @param applicationService the application service to help with mapping
     */
    @Autowired
    public ApplicationRequestMapperImpl(UserService userService, ApplicationService applicationService) {
        this.userService = userService;
        this.applicationService = applicationService;
    }

    /**
     * Maps the create draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public Application createDraftRequestToDraft(CreateDraftApplicationRequest request) {
        return new DraftApplication(null, null, userService.loadUser(request.getUsername()), request.getApplicationTemplate(),
                request.getAnswers());
    }

    /**
     * Mao the application to a draft or referred application
     * @param request the request being mapped
     * @param loaded the loaded application
     * @param status the expected status of the application
     * @return the mapped application
     */
    private Application mapDraftOrReferred(UpdateDraftApplicationRequest request, Application loaded, ApplicationStatus status) {
        if (loaded != null) {
            if (loaded.getStatus() != status)
                throw new MappingException("The application with ID " + request.getId() + " does not have the status " + status);

            loaded.setAnswers(request.getAnswers());

            List<AttachedFile> attachedFiles = loaded.getAttachedFiles();
            attachedFiles.clear();
            request.getAttachedFiles().forEach(loaded::attachFile);

            return loaded;
        } else {
            return null;
        }
    }

    /**
     * Maps the update draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public Application updateDraftRequestToDraft(UpdateDraftApplicationRequest request) throws MappingException {
        String id = request.getId();
        Application loaded = applicationService.getApplication(id);

        return this.mapDraftOrReferred(request, loaded, ApplicationStatus.DRAFT);
    }

    /**
     * Maps the update request to a referred application
     * @param request the request to map
     * @return the mapped referred application
     * @throws MappingException if the request ID does not match a DraftApplication
     */
    @Override
    public Application updateRequestToReferred(UpdateDraftApplicationRequest request) throws MappingException {
        String id = request.getId();
        Application loaded = applicationService.getApplication(id);

        return this.mapDraftOrReferred(request, loaded, ApplicationStatus.REFERRED);
    }

    /**
     * Maps submit application request to an application. The application that is returned should be either a draft
     * or referred application
     *
     * @param request the request to map
     * @return the mapped application, null if it does not exist
     * @throws MappingException if the request ID does not match a draft or referred application
     */
    @Override
    public Application submitRequestToApplication(SubmitApplicationRequest request) throws MappingException {
        String id = request.getId();
        Application loaded = applicationService.getApplication(id);

        if (loaded != null) {
            if (!Set.of(ApplicationStatus.DRAFT, ApplicationStatus.REFERRED).contains(loaded.getStatus()))
                throw new MappingException("The application with ID " + id + " is not in the draft or referred states");

            return loaded;
        } else {
            return null;
        }
    }

    /**
     * Maps refer application request to the MappedReferApplicationRequest object. Does not perform validation to
     * ensure that the application or user exists.
     *
     * @param request the request to map
     * @return the mapped request object
     */
    @Override
    public MappedReferApplicationRequest mapReferApplicationRequest(ReferApplicationRequest request) {
        return new MappedReferApplicationRequest(applicationService.getApplication(request.getId()),
                request.getEditableFields(), userService.loadUser(request.getReferrer()));
    }

    /**
     * Map the request to an object with the loaded application and list of loaded committee members
     *
     * @param request the request to map
     * @return the mapped request
     */
    @Override
    public MappedAcceptResubmittedRequest mapAcceptResubmittedRequest(AcceptResubmittedRequest request) {
        Application application = applicationService.getApplication(request.getId());
        List<User> committeeMembers = request.getCommitteeMembers()
                .stream()
                .map(userService::loadUser)
                .collect(Collectors.toList());

        return new MappedAcceptResubmittedRequest(application, committeeMembers);
    }

    /**
     * Map the request comment to the comment entity
     * @param comment the request comment
     * @return the mapped comment entity
     */
    public Comment mapComment(ReviewSubmittedApplicationRequest.Comment comment) {
        Comment mapped = new Comment(comment.getId(), userService.loadUser(comment.getUsername()), comment.getComment(),
                comment.getComponentId(), new ArrayList<>(), comment.getCreatedAt(), comment.isSharedApplicant(), comment.isSharedReviewer());
        mapped.setEdited(comment.isEdited());

        if (mapped.getUser() == null)
            throw new MappingException("A comment cannot exist with a null user");

        for (ReviewSubmittedApplicationRequest.Comment sub : comment.getSubComments())
            mapped.addSubComment(mapComment(sub));

        return mapped;
    }

    /**
     * Map the request comments to real comments
     * @param comments the comments to map
     * @return the list of comments
     */
    private List<Comment> mapComments(List<ReviewSubmittedApplicationRequest.Comment> comments) {
        return comments.stream()
                .map(this::mapComment)
                .collect(Collectors.toList());
    }

    /**
     * Map the request to a submitted application with the comments mapped and added to the application
     *
     * @param request the request to map
     * @return the submitted application
     * @throws MappingException       if any user in the comments are null;
     * @throws InvalidStatusException if the application is not in a review state
     */
    @Override
    public Application reviewSubmittedRequestToSubmitted(ReviewSubmittedApplicationRequest request) throws MappingException, InvalidStatusException {
        Application loaded = applicationService.getApplication(request.getId());
        ApplicationStatus status = (loaded == null) ? null:loaded.getStatus();

        if (loaded == null) {
            return null;
        } else if (status != ApplicationStatus.REVIEW && status != ApplicationStatus.REVIEWED) {
            throw new InvalidStatusException("The application must be in a " + ApplicationStatus.REVIEW + " or " + ApplicationStatus.REVIEWED + " status");
        } else {
            mapComments(request.getComments()).forEach(loaded::addComment);

            return loaded;
        }
    }

    /**
     * Maps the approve application request
     *
     * @param request the request to map
     * @return the mapped request
     */
    @Override
    public MappedApprovalRequest mapApprovalRequest(ApproveApplicationRequest request) {
        Application application = applicationService.getApplication(request.getId());
        ReviewSubmittedApplicationRequest.Comment finalComment = request.getFinalComment();
        Comment mapped = (finalComment == null) ? null:new Comment(null,
                userService.loadUser(finalComment.getUsername()), finalComment.getComment(), null, new ArrayList<>());

        return new MappedApprovalRequest(application, request.isApprove(), mapped);
    }
}
