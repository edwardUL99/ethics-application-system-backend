package ie.ul.edward.ethics.applications.parsing;

import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;
import org.springframework.core.io.Resource;

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
    ParsedApplication[] parse(InputStream inputStream) throws ApplicationParseException;
}
