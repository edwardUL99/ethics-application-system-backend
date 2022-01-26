package ie.ul.ethics.scieng.applications.models.applications;

import java.util.Arrays;

/**
 * This enum provides the statuses of an application
 */
public enum ApplicationStatus {
    /**
     * This status represents an application in the draft stage. The only application that should have this status
     * is a DraftApplication, i.e. other implementations must check the value of ApplicationStatus being assigned to it and
     * prevent DRAFT being set to it
     */
    DRAFT("Draft"),
    /**
     * This status represents an application in the submitted stage
     */
    SUBMITTED("Submitted"),
    /**
     * This status represents an application that has been referred but re-submitted back to the ethics committee
     */
    RESUBMITTED("Re-submitted"),
    /**
     * This status represents an application that is currently being reviewed
     */
    REVIEW("In Review"),
    /**
     * This status represents an application that has been reviewed and is pending further action from the chair/administrator
     */
    REVIEWED("Reviewed"),
    /**
     * This status represents an application that has been referred to the applicant for more information
     */
    REFERRED("Referred to Applicant"),
    /**
     * This status represents an application that has been approved by the committee
     */
    APPROVED("Approved"),
    /**
     * This status represents an application that has been rejected by the committee
     */
    REJECTED("Rejected");

    /**
     * The label of the status
     */
    private final String label;

    /**
     * Construct an application status object
     * @param label the label for the enum value
     */
    ApplicationStatus(String label) {
        this.label = label;
    }

    /**
     * Get an ApplicationStatus enum value from the label
     * @param label the label to find the enum value for
     * @return the enum value
     * @throws IllegalArgumentException if the label has no candidate enum value
     */
    public static ApplicationStatus fromLabel(String label) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The label " + label + " does not match any enum value"));
    }

    /**
     * Retrieve the label value
     * @return the label value of the enum
     */
    public String label() {
        return label;
    }
}
