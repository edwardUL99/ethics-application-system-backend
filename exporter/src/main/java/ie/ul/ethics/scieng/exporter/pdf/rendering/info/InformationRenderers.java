package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A registration class for ApplicationInfo implementations
 */
public final class InformationRenderers {
    /**
     * The registered ApplicationInfo objects
     */
    private static final Map<ApplicationStatus, ApplicationInfo> registered = new HashMap<>();

    static {
        ApplicationInfo defaultInfo = new DefaultApplicationInfo();
        ApplicationInfo submittedInfo = new SubmittedApplicationInfo();
        ApplicationInfo referredInfo = new ReferredApplicationInfo();

        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status == ApplicationStatus.REFERRED) {
                registered.put(status, referredInfo);
            } else if (status == ApplicationStatus.DRAFT) {
                registered.put(status, defaultInfo);
            } else {
                registered.put(status, submittedInfo);
            }
        }
    }

    /**
     * Get the implementation for the given status
     * @param status the status to implement for
     * @return the implemented status
     */
    public static ApplicationInfo getApplicationInfo(ApplicationStatus status) {
        return registered.getOrDefault(status, new DefaultApplicationInfo());
    }
}
