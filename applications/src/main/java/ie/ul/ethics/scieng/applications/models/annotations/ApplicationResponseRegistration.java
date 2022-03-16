package ie.ul.ethics.scieng.applications.models.annotations;

import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to register an application response
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationResponseRegistration {
    /**
     * The status(es) that the application response is being configured for
     * @return the array of statuses the response is being registered for
     */
    ApplicationStatus[] status();
}
