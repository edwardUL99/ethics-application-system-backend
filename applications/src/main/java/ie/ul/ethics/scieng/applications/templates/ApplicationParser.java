package ie.ul.ethics.scieng.applications.templates;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.io.InputStream;

/**
 * This interface provides a means of parsing and validating input streams into application templates. It can parse multiple
 * streams at one time.
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
