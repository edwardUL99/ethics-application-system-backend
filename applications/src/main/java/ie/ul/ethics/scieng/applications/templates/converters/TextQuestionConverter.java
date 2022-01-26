package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.*;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This class converts maps into text questions
 */
@Converter(ComponentType.TEXT_QUESTION)
public class TextQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.TEXT_QUESTION, map.keySet(), "title", "name");
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

        return new TextQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.TEXT_QUESTION, "description", map.getOrDefault("description", null)),
                (boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED), (boolean)map.getOrDefault("singleLine", true),
                (String)map.getOrDefault("questionType", "text"));
    }
}
