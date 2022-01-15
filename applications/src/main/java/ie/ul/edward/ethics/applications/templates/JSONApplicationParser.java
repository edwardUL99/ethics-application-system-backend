package ie.ul.edward.ethics.applications.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.ul.edward.ethics.applications.templates.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.templates.converters.Converters;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;
import org.springframework.stereotype.Component;

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
    private ApplicationTemplate parseApplication(Map<String, Object> map) {
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

        return new ApplicationTemplate(
                (String)map.get("id"),
                (String)map.get("name"),
                (String)map.get("description"),
                (String)map.get("version"),
                components
        );
    }

    /**
     * Parse the provided input streams into application(s). The applications will be returned in the array, otherwise, it will be an
     * array with one element
     * @param inputStreams the input streams of the application files to parse
     * @return the array of parsed applications
     * @throws ApplicationParseException if the application being parsed is not valid or another exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public ApplicationTemplate[] parse(InputStream...inputStreams) throws ApplicationParseException {
        List<ApplicationTemplate> applications = new ArrayList<>();

        for (InputStream stream : inputStreams) {
            try {
                Object jsonObject = objectMapper.readValue(stream, Object.class);
                Map<String, Object> map = (Map<String, Object>) jsonObject;

                applications.add(parseApplication(map));
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ApplicationParseException("Failed to parse application JSON", ex);
            }
        }

        ApplicationTemplate[] applications1 = new ApplicationTemplate[applications.size()];

        return applications.toArray(applications1);
    }
}
