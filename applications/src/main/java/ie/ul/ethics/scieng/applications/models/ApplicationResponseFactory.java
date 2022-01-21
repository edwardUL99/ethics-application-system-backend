package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The factory for the application response object creation
 */
public final class ApplicationResponseFactory {
    /**
     * The map of application statuses to the class object of the ApplicationResponse subclass
     */
    private static final Map<ApplicationStatus, Class<? extends ApplicationResponse>> responseClasses =
            new HashMap<>();

    static {
        register(ApplicationStatus.DRAFT, DraftApplicationResponse.class);
        Set.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW,
                ApplicationStatus.REVIEWED, ApplicationStatus.APPROVED, ApplicationStatus.REJECTED)
                .forEach(status -> register(status, SubmittedApplicationResponse.class));
        register(ApplicationStatus.REFERRED, ReferredApplicationResponse.class);
    }

    /**
     * Register the status with the associated class of the response object
     * @param status the status to register
     * @param cls the class to associate with the status
     * @throws IllegalArgumentException if the class does not have a constructor that takes an Application as its only parameter
     */
    private static void register(ApplicationStatus status, Class<? extends ApplicationResponse> cls) throws IllegalArgumentException {
        try {
            cls.getDeclaredConstructor(Application.class);
            responseClasses.put(status, cls);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("No constructor that takes a single Application parameter found in class " + cls);
        }
    }

    /**
     * Construct the appropriate ApplicationResponse for the provided application
     * @param application the application to construct the response from
     * @return the ApplicationResponse object, null if the status has no associated response object
     */
    public static ApplicationResponse buildResponse(Application application) {
        Class<? extends ApplicationResponse> cls = responseClasses.get(application.getStatus());

        if (cls == null) {
            return null;
        } else {
            try {
                return cls.getDeclaredConstructor(Application.class).newInstance(application);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to construct an instance of class " + cls, ex);
            }
        }
    }
}
