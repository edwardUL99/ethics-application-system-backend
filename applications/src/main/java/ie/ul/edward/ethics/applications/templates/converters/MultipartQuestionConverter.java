package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.*;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This class represents a converter that can convert a map into a MultipartQuestion
 */
@Converter(ComponentTypes.MULTIPART_QUESTION)
public class MultipartQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("title", "conditional", "parts"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The multipart question component is missing keys");

        if (!Map.class.isAssignableFrom(map.get("parts").getClass()))
            throw new ApplicationParseException("The parts field must map to a map");
    }

    /**
     * Convert the branch map to the QuestionBranch
     * @param branch the branch map to convert
     * @return the converted branch
     */
    private MultipartQuestionComponent.QuestionBranch convertBranch(Map<String, Object> branch) {
        return new MultipartQuestionComponent.QuestionBranch((String)branch.get("part"), (String)branch.get("value"));
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
    @SuppressWarnings("unchecked")
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);
        Map<String, MultipartQuestionComponent.QuestionPart> parts = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> e : ((Map<String, Map<String, Object>>)map.get("parts")).entrySet()) {
            String part = e.getKey();
            Map<String, Object> partMap = e.getValue();

            if (!partMap.containsKey("question"))
                throw new ApplicationParseException("A question part needs to contain a question");

            if (!partMap.containsKey("branches"))
                throw new ApplicationParseException("A question part needs to contain a branches list");

            Map<String, Object> question = (Map<String, Object>)partMap.get("question");
            QuestionComponent questionComponent = (QuestionComponent)Converters.getConverter((String)question.get("type"));
            List<MultipartQuestionComponent.QuestionBranch> branches = new ArrayList<>();

            for (Map<String, Object> branch : (List<Map<String, Object>>)partMap.get("branches")) {
                branches.add(convertBranch(branch));
            }

            parts.put(part, new MultipartQuestionComponent.QuestionPart(questionComponent, branches));
        }

        return new MultipartQuestionComponent((boolean)map.get("conditional"), parts);
    }
}
