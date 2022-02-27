package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionTableComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents a converter for converting a QuestionTable component
 */
@Converter(ComponentType.QUESTION_TABLE)
public class QuestionTableConverter extends BaseConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    @SuppressWarnings("unchecked")
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.QUESTION_TABLE, map.keySet(), "columns", "numRows");

        Object columns = map.get("columns");

        if (!Map.class.isAssignableFrom(columns.getClass()))
            throw new ApplicationParseException("The columns field of the " + ComponentType.QUESTION_TABLE.label() + " component must be a map of the column name to its corresponding question");

        Map<String, Object> columnsMap = (Map<String, Object>) columns;

        columnsMap.forEach((k, v) -> {
            if (!Map.class.isAssignableFrom(v.getClass()))
                throw new ApplicationParseException("Each column in columns must map to a question component");
        });

        if (!Integer.class.isAssignableFrom(map.get("numRows").getClass()))
            throw new ApplicationParseException("The numRows field of the " + ComponentType.QUESTION_TABLE.label() + " component must be an Integer");
    }

    /**
     * Create the base component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ApplicationComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        Map<String, Map<String, Object>> columns = (Map<String, Map<String, Object>>) map.get("columns");
        int numRows = (int) map.get("numRows");

        Map<String, QuestionTableComponent.Cells> columnsMap = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> e : columns.entrySet()) {
            String column = e.getKey();
            Map<String, Object> question = e.getValue();
            question.put("title", null);

            List<QuestionComponent> questionComponents = new ArrayList<>();

            for (int i = 0; i < numRows; i++) {
                ApplicationComponent applicationComponent = Converters.getConverter((String)question.get("type")).convert(question);

                if (!(applicationComponent instanceof QuestionComponent))
                    throw new ApplicationParseException("Cells of the QuestionTable must be a QuestionComponent");

                QuestionComponent questionComponent = (QuestionComponent) applicationComponent;

                questionComponent.setName(questionComponent.getName() + "_" + (i+1));
                questionComponent.setComponentId(questionComponent.getName());
                questionComponents.add(questionComponent);
            }

            columnsMap.put(column, new QuestionTableComponent.Cells(null, column, questionComponents));
        }

        return new QuestionTableComponent(columnsMap);
    }
}
