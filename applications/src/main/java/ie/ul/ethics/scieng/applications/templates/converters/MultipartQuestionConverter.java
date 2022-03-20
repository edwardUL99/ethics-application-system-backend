package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.*;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This class represents a converter that can convert a map into a MultipartQuestion
 */
@Converter(ComponentType.MULTIPART_QUESTION)
public class MultipartQuestionConverter extends QuestionConverter {
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
        MultipartQuestionComponent.QuestionBranch parsed = new MultipartQuestionComponent.QuestionBranch((String)branch.get("part"), (String)branch.get("value"));
        parsed.setBranchId(ComponentConverter.parseDatabaseId(branch.getOrDefault("branchId", null)));

        return parsed;
    }

    /**
     * Create the base question component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected QuestionComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        Map<String, MultipartQuestionComponent.QuestionPart> parts = new HashMap<>();
        MultipartQuestionComponent multipart = new MultipartQuestionComponent();

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

            Long id = ComponentConverter.parseDatabaseId(partMap.getOrDefault("id", null));

            parts.put(part, new MultipartQuestionComponent.QuestionPart(id, part, questionComponent, branches));
        }

        multipart.setTitle((String)map.getOrDefault("title", null));
        multipart.setRequired((boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED));
        multipart.setConditional((boolean)map.get("conditional"));
        multipart.setParts(parts);

        return multipart;
    }
}
