package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.applications.templates.components.SignatureQuestionComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This class converts maps into signature questions
 */
@Converter(ComponentType.SIGNATURE)
public class SignatureQuestionConverter extends QuestionConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.SIGNATURE, map.keySet(), "title", "name", "label");
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
        return new SignatureQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.SIGNATURE, "description", map.getOrDefault("description", null)),
                (String)map.get("label"), (boolean)map.getOrDefault("required", true));
    }
}
