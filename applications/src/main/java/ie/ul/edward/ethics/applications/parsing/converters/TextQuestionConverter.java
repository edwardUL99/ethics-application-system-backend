package ie.ul.edward.ethics.applications.parsing.converters;

import ie.ul.edward.ethics.applications.parsing.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.parsing.components.QuestionComponent;
import ie.ul.edward.ethics.applications.parsing.components.SignatureQuestionComponent;
import ie.ul.edward.ethics.applications.parsing.components.TextQuestionComponent;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class converts maps into text questions
 */
public class TextQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("title", "description", "singleLine"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The signature question component is missing keys");
    }

    /**
     * Convert the provided map to the equivalent ApplicationComponent.
     * Should call the validate method to ensure the map is valid
     *
     * @param map the map to convert
     * @return the equivalent application component
     * @throws ApplicationParseException if the map isn't valid or an error occurs
     */
    @Override
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);

        return new TextQuestionComponent((String)map.get("title"), (String)map.get("description"), (boolean)map.getOrDefault("required", false), (boolean)map.getOrDefault("singleLine", true));
    }
}
