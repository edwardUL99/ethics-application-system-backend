package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

/**
 * This service is used to execute a request and respond with an application that needs to be cleaned.
 * If the response should contain all the information without cleaning it for view by a user, this service should not be used
 */
public interface ApplicationResponseService {
    /**
     * Perform the task and return the response
     * @param task the task to perform
     * @return the response body
     */
    ResponseEntity<?> process(Function<Void, Application> task);

    /**
     * Perform the task and return the response
     * @param task the task to perform
     * @param status a different status to specify
     * @return the response body
     */
    ResponseEntity<?> process(Function<Void, Application> task, HttpStatus status);

    /**
     * A task can throw this exception to interrupt the processing and return the provided response instead
     */
    class TaskInterrupt extends RuntimeException {
        /**
         * The response to return in an interruption
         */
        private final ResponseEntity<?> response;

        /**
         * Create the interruption with the provided response
         * @param response the response to interrupt the task with and return
         */
        public TaskInterrupt(ResponseEntity<?> response) {
            this.response = response;
        }

        /**
         * Retrieve the response
         * @return the response to return in an interruption
         */
        public ResponseEntity<?> getResponse() {
            return response;
        }
    }
}
