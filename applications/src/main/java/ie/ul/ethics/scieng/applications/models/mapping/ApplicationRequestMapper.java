package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;

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
    DraftApplication createDraftRequestToDraft(CreateDraftApplicationRequest request);

    /**
     * Maps the update draft request to the draft application
     * @param request the request to map
     * @return the mapped draft application
     * @throws IllegalStateException if the request ID does not match a DraftApplication
     */
    DraftApplication updateDraftRequestToDraft(UpdateDraftApplicationRequest request);
}
