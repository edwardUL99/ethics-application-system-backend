package ie.ul.edward.ethics.applications.templates;

import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.io.InputStream;

/**
 * This interface provides a means of parsing and validating a JSON application into a map
 */
public interface ApplicationParser {
    /**
     * Parse the provided input streams into application(s). The applications will be returned in the array, otherwise, it will be an
     * array with one element
     * @param inputStreams the input streams of the application files to parse
     * @return the array of parsed applications
     * @throws ApplicationParseException if the application being parsed is not valid or another exception occurs
     */
    ApplicationTemplate[] parse(InputStream...inputStreams) throws ApplicationParseException;
}
