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
import ie.ul.ethics.scieng.applications.models.applications.Comment;

/**
 * This interface represents a mapper that can map any requests to the applications module into mapped objects.
 * It is not intended to perform error checking (for example, to check if required fields are not null etc.). This must
 * be done by the client using the interface
 */
public interface ApplicationRequestMapper {
    /**
     * Maps the create draft request to the draft application
     * @param request the request to map
     * @return the mapped draft application
     */
    Application createDraftRequestToDraft(CreateDraftApplicationRequest request);

    /**
     * Maps the update draft request to the draft application
     * @param request the request to map
     * @return the mapped draft application
     * @throws MappingException if the request ID does not match a DraftApplication
     */
    Application updateDraftRequestToDraft(UpdateDraftApplicationRequest request) throws MappingException;

    /**
     * Maps the update request to a referred application
     * @param request the request to map
     * @return the mapped referred application
     * @throws MappingException if the request ID does not match a DraftApplication
     */
    Application updateRequestToReferred(UpdateDraftApplicationRequest request) throws MappingException;

    /**
     * Maps submit application request to an application. The application that is returned should be either a draft
     * or referred application
     * @param request the request to map
     * @return the mapped application, null if it does not exist
     * @throws MappingException if the request ID does not match a draft or referred application
     */
    Application submitRequestToApplication(SubmitApplicationRequest request) throws MappingException;

    /**
     * Maps refer application request to the MappedReferApplicationRequest object. Does not perform validation to
     * ensure that the application or user exists.
     * @param request the request to map
     * @return the mapped request object
     */
    MappedReferApplicationRequest mapReferApplicationRequest(ReferApplicationRequest request);

    /**
     * Map the request to an object with the loaded application and list of loaded committee members
     * @param request the request to map
     * @return the mapped request
     */
    MappedAcceptResubmittedRequest mapAcceptResubmittedRequest(AcceptResubmittedRequest request);

    /**
     * Map the request to a submitted application with the comments mapped and added to the application
     * @param request the request to map
     * @return the submitted application
     * @throws MappingException if any user in the comments are null;
     * @throws InvalidStatusException if the application is not in a review/reviewed state
     */
    Application reviewSubmittedRequestToSubmitted(ReviewSubmittedApplicationRequest request) throws MappingException,
            InvalidStatusException;

    /**
     * Maps the approve application request
     * @param request the request to map
     * @return the mapped request
     */
    MappedApprovalRequest mapApprovalRequest(ApproveApplicationRequest request);

    /**
     * Map the request comment to a comment entity
     * @param comment the comment to map
     * @return the mapped comment or null if any comment user does not exist
     */
    Comment mapComment(ReviewSubmittedApplicationRequest.Comment comment);
}
