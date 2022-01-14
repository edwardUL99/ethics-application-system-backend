package ie.ul.edward.ethics.applications.parsing;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.ul.edward.ethics.applications.parsing.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.parsing.converters.Converters;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Implementation of the application parser interface
 */
@Component
public class JSONApplicationParser implements ApplicationParser {
    /**
     * The mapper for mapping JSON
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse the application map
     * @param map the map to parse
     * @return the parsed application
     */
    @SuppressWarnings("unchecked")
    private ParsedApplication parseApplication(Map<String, Object> map) {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("id", "name", "description", "version", "components"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The application is missing keys");

        if (!List.class.isAssignableFrom(map.get("components").getClass()))
            throw new ApplicationParseException("The components field must map to a List");

        List<ApplicationComponent> components = new ArrayList<>();

        for (Map<String, Object> component : (List<Map<String, Object>>)map.get("components")) {
            if (!component.containsKey("type")) {
                throw new ApplicationParseException("A component of an application must contain the type field");
            }

            components.add(Converters.getConverter((String)component.get("type")).convert(component));
        }

        return new ParsedApplication(
                (String)map.get("id"),
                (String)map.get("name"),
                (String)map.get("description"),
                (String)map.get("version"),
                components
        );
    }

    /**
     * Parse the provided resource into application(s). If the resource represents multiple applications (for example, an
     * expedited and full application form), the applications will be returned in the array, otherwise, it will be an
     * array with one element
     *
     * @param inputStream the input stream of the json to parse
     * @return the array of parsed applications
     * @throws ApplicationParseException if the application being parsed is not valid or another exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public ParsedApplication[] parse(InputStream inputStream) throws ApplicationParseException {
        List<ParsedApplication> applications = new ArrayList<>();

        try {
            Object jsonObject = objectMapper.readValue(inputStream, Object.class);
            Map<String, Object> map = (Map<String, Object>) jsonObject;

            if (!map.containsKey("applications"))
                throw new ApplicationParseException("The applications file needs to be a JSON object with a key applications mapping to application objects");

            for (Map.Entry<String, Map<String, Object>> e : ((Map<String, Map<String, Object>>)map.get("applications")).entrySet()) {
                applications.add(parseApplication(e.getValue()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ApplicationParseException("Failed to parse application JSON", ex);
        }

        ParsedApplication[] applications1 = new ParsedApplication[applications.size()];

        return applications.toArray(applications1);
    }
}
