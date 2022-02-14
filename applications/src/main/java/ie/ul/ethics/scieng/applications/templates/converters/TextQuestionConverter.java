package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.*;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This class converts maps into text questions
 */
@Converter(ComponentType.TEXT_QUESTION)
public class TextQuestionConverter extends QuestionConverter {
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
     * Create the base question component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    protected QuestionComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        return new TextQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.TEXT_QUESTION, "description", map.getOrDefault("description", null)),
                (boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED), (boolean)map.getOrDefault("singleLine", true),
                (String)map.getOrDefault("questionType", "text"));
    }
}
