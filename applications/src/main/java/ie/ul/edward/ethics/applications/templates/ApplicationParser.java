package ie.ul.edward.ethics.applications.templates;

import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.io.InputStream;

/**
 * This interface provides a means of parsing and validating a JSON application into a map
 */
public interface ApplicationParser {
    /**
     * Parse the provided resource into application(s). If the resource represents multiple applications (for example, an
     * expedited and full application form), the applications will be returned in the array, otherwise, it will be an
     * array with one element
     * @param inputStream the input stream of the application file to parse
     * @return the array of parsed applications
     * @throws ApplicationParseException if the application being parsed is not valid or another exception occurs
     */
    ApplicationTemplate[] parse(InputStream inputStream) throws ApplicationParseException;
}
