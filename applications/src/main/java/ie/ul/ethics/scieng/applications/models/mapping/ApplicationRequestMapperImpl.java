package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.exceptions.MappingException;
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
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.files.exceptions.FileException;
import ie.ul.ethics.scieng.files.services.FileService;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * The file service used for deleting old files
     */
    private final FileService fileService;

    /**
     * Create an ApplicationRequestMapperImpl
     * @param userService the user service to help with mapping
     * @param applicationService the application service to help with mapping
     * @param fileService the fileService to delete old files
     */
    @Autowired
    public ApplicationRequestMapperImpl(UserService userService, ApplicationService applicationService, FileService fileService) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.fileService = fileService;
    }

    /**
     * Maps the create draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public DraftApplication createDraftRequestToDraft(CreateDraftApplicationRequest request) {
        return new DraftApplication(null, request.getId(), userService.loadUser(request.getUsername()), request.getApplicationTemplate(),
                request.getValues());
    }

    /**
     * Maps the update draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public DraftApplication updateDraftRequestToDraft(UpdateDraftApplicationRequest request) throws MappingException {
        String id = request.getId();
        Application loaded = applicationService.getApplication(id);

        if (loaded != null) {
            if (loaded.getStatus() != ApplicationStatus.DRAFT)
                throw new MappingException("The application with ID " + id + " is not a DraftApplication");

            DraftApplication draftApplication = (DraftApplication) loaded;
            draftApplication.getAnswers().putAll(request.getAnswers());

            Map<String, AttachedFile> currentFiles = draftApplication.getAttachedFiles();
            Map<String, AttachedFile> newFiles = request.getAttachedFiles();

            String username = loaded.getUser().getUsername();

            for (Map.Entry<String, AttachedFile> e : newFiles.entrySet()) {
                String key = e.getKey();
                AttachedFile value = e.getValue();
                AttachedFile current = currentFiles.get(key);

                try {
                    if (current != null && !current.equals(value))
                        fileService.deleteFile(current.getFilename(), current.getDirectory(), username);
                } catch (FileException ex) {
                    ex.printStackTrace();
                    log.error("Failed to delete an AttachedFile that was meant to exist, overwriting with new file");
                }

                currentFiles.put(key, value);
            }

            return draftApplication;
        } else {
            return null;
        }
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
    private Comment mapComment(ReviewSubmittedApplicationRequest.Comment comment) {
        Comment mapped = new Comment(comment.getId(), userService.loadUser(comment.getUsername()), comment.getComment(),
                comment.getComponentId(), new ArrayList<>(), comment.getCreatedAt());

        if (mapped.getUser() == null)
            throw new MappingException("A comment cannot exist with a null user");

        for (ReviewSubmittedApplicationRequest.Comment sub : comment.getSubComments()) {
            mapped.addSubComment(mapComment(sub));
        }

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
    public SubmittedApplication reviewSubmittedRequestToSubmitted(ReviewSubmittedApplicationRequest request) throws MappingException, InvalidStatusException {
        Application loaded = applicationService.getApplication(request.getId());

        if (loaded == null) {
            return null;
        } else if (loaded.getStatus() != ApplicationStatus.REVIEW) {
            throw new InvalidStatusException("The application must be in a " + ApplicationStatus.REVIEW + " status");
        } else {
            SubmittedApplication submitted = (SubmittedApplication) loaded;
            mapComments(request.getComments()).forEach(submitted::addComment);

            return submitted;
        }
    }
}
