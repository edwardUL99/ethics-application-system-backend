package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.*;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.MultipartQuestionComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

import java.util.*;

/**
 * This class represents a converter that can convert a map into a MultipartQuestion
 */
@Converter(ComponentType.MULTIPART_QUESTION)
public class MultipartQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.MULTIPART_QUESTION, map.keySet(), "conditional", "parts");

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
        MultipartQuestionComponent multipart = new MultipartQuestionComponent();

        int sequenceId = 0;
        for (Map.Entry<String, Map<String, Object>> e : ((Map<String, Map<String, Object>>)map.get("parts")).entrySet()) {
            String part = e.getKey();
            Map<String, Object> partMap = e.getValue();

            if (!partMap.containsKey("question"))
                throw new ApplicationParseException("A question part needs to contain a question");

            if (!partMap.containsKey("branches"))
                throw new ApplicationParseException("A question part needs to contain a branches list");

            Map<String, Object> question = (Map<String, Object>)partMap.get("question");
            QuestionComponent questionComponent = (QuestionComponent)Converters.getConverter((String)question.get("type")).convert(question);
            List<MultipartQuestionComponent.QuestionBranch> branches = new ArrayList<>();

            for (Map<String, Object> branch : (List<Map<String, Object>>)partMap.get("branches")) {
                branches.add(convertBranch(branch));
            }

            questionComponent.setComponentId(multipart.getComponentId() + "_" + ++sequenceId);
            parts.put(part, new MultipartQuestionComponent.QuestionPart(null, part, questionComponent, branches));
        }

        multipart.setTitle((String)map.getOrDefault("title", null));
        multipart.setRequired((boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED));
        multipart.setConditional((boolean)map.get("conditional"));
        multipart.setParts(parts);

        return multipart;
    }
}
