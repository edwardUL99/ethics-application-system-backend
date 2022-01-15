package ie.ul.edward.ethics.applications.templates.components;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This branch represents a branch that results in a container being replaced by another container
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ReplacementBranch extends Branch {
    /**
     * The list of replacements
     */
    private List<Replacement> replacements;

    /**
     * Create a default ReplacementBranch
     */
    public ReplacementBranch() {
        this(new ArrayList<>());
    }

    /**
     * Create a ReplacementBranch
     *
     * @param replacements the list of replacements if this branch is triggered
     */
    public ReplacementBranch(List<Replacement> replacements) {
        super(ComponentTypes.REPLACEMENT_BRANCH);
        this.replacements = replacements;
    }

    /**
     * This class represents a replacement of one container with another
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Replacement {
        /**
         * The container ID to replace
         */
        private String replaceId;
        /**
         * The ID of the container to add into the replacement. The ID can be [application-id]-[containerId]
         */
        private String targetId;
    }
}
