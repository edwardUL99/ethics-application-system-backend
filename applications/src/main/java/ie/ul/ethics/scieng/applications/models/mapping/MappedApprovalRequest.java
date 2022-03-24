package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a mapped approval request
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MappedApprovalRequest {
    /**
     * The mapped application
     */
    private Application application;
    /**
     * The approval flag
     */
    private boolean approve;
    /**
     * The mapped final comment
     */
    private Comment finalComment;
}
