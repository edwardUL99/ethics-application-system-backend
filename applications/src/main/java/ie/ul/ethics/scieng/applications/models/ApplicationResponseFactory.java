package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * The factory for the application response object creation
 */
public final class ApplicationResponseFactory {
    /**
     * The map of application statuses to the class object of the ApplicationResponse subclass
     */
    private static final Map<ApplicationStatus, Class<? extends ApplicationResponse>> responseClasses =
            new HashMap<>();

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
     * Register all ApplicationResponseRegistration annotated classes
     */
    @SuppressWarnings("unchecked")
    public static void register() {
        Reflections reflections = new Reflections("ie.ul.ethics.scieng.applications.models");

        for (Class<?> cls : reflections.get(TypesAnnotated.with(ApplicationResponseRegistration.class).asClass())) {
            if (!ApplicationResponse.class.isAssignableFrom(cls) && cls != ApplicationResponse.class)
                throw new ApplicationException("A class annotated with ApplicationResponseRegistration must be a subclass of ApplicationResponse");

            ApplicationResponseRegistration registration = cls.getAnnotation(ApplicationResponseRegistration.class);
            ApplicationStatus[] statuses = registration.status();
            Class<? extends ApplicationResponse> responseClass = (Class<? extends ApplicationResponse>) cls;

            for (ApplicationStatus status : statuses)
                register(status, responseClass);
        }
    }

    /**
     * Construct the appropriate ApplicationResponse for the provided application
     * @param application the application to construct the response from
     * @return the ApplicationResponse object, null if the status has no associated response object
     */
    public static ApplicationResponse buildResponse(Application application) {
        ApplicationStatus status = application.getStatus();
        Class<? extends ApplicationResponse> cls = responseClasses.get(status);

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
