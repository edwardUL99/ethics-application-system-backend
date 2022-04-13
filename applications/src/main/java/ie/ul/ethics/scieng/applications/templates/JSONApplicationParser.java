package ie.ul.ethics.scieng.applications.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.converters.Converters;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Implementation of the application parser interface
 */
@Component
@Log4j2
public class JSONApplicationParser implements ApplicationParser {
    /**
     * The mapper for mapping JSON
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                null,
                (String)map.get("id"),
                (String)map.get("name"),
                Converters.parseLongString(null, "description", map.get("description")),
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

                ApplicationTemplate parsed = parseApplication(map);
                log.debug("Parsed application template with id: {}, and name: {}", parsed.getId(), parsed.getName());
                applications.add(parsed);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ApplicationParseException("Failed to parse application JSON", ex);
            }
        }

        ApplicationTemplate[] applications1 = new ApplicationTemplate[applications.size()];

        return applications.toArray(applications1);
    }
}
