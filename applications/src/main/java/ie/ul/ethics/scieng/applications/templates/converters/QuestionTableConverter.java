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
        Converters.validateKeys(ComponentType.QUESTION_TABLE, map.keySet(), "cells", "numRows");

        Object cells = map.get("cells");

        if (!Map.class.isAssignableFrom(cells.getClass()))
            throw new ApplicationParseException("The cells field of the " + ComponentType.QUESTION_TABLE.label() + " component must be a map of the column name to its corresponding question");

        Map<String, Object> cellsMap = (Map<String, Object>) cells;

        if (!cellsMap.containsKey("columns"))
            throw new ApplicationParseException("The question-table component cells field is missing the key columns");

        Map<String, Object> columnsMap = (Map<String, Object>) cellsMap.get("columns");

        columnsMap.forEach((k, v) -> {
            if (v != null && !Map.class.isAssignableFrom(v.getClass()))
                throw new ApplicationParseException("Each column in columns must map to a question component");
        });

        if (!Integer.class.isAssignableFrom(map.get("numRows").getClass()))
            throw new ApplicationParseException("The numRows field of the " + ComponentType.QUESTION_TABLE.label() + " component must be an Integer");
    }

    /**
     * Parses the cells mapping component
     * @param numRows the number of rows in the table
     * @param mapping the mapping object
     * @return the parsed cells mapping
     */
    @SuppressWarnings("unchecked")
    private QuestionTableComponent.CellsMapping parseCellsMapping(int numRows, Map<String, Object> mapping) {
        Long mappingId = ComponentConverter.parseDatabaseId(mapping.getOrDefault("databaseId", null));

        Map<String, Object> columns = (Map<String, Object>) mapping.get("columns");
        Map<String, QuestionTableComponent.Cells> parsedCells = new HashMap<>();

        for (Map.Entry<String, Object> e : columns.entrySet()) {
            String column = e.getKey();
            Map<String, Object> value = (Map<String, Object>) e.getValue();

            if (value.containsKey("components")) {
                Long dbId = ComponentConverter.parseDatabaseId(value.getOrDefault("databaseId", null));
                List<Map<String, Object>> components = (List<Map<String, Object>>) value.get("components");

                List<QuestionComponent> questionComponents = new ArrayList<>();
                for (Map<String, Object> component : components) {
                    component.put("title", null);
                    ApplicationComponent applicationComponent = Converters.getConverter((String) component.get("type")).convert(component);

                    if (!(applicationComponent instanceof QuestionComponent))
                        throw new ApplicationParseException("Cells of the QuestionTable must be a QuestionComponent");

                    questionComponents.add((QuestionComponent) applicationComponent);
                }

                parsedCells.put(column, new QuestionTableComponent.Cells(dbId, column, questionComponents));
            } else {
                value.put("title", null);

                List<QuestionComponent> questionComponents = new ArrayList<>();

                for (int i = 0; i < numRows; i++) {
                    ApplicationComponent applicationComponent = Converters.getConverter((String) value.get("type")).convert(value);

                    if (!(applicationComponent instanceof QuestionComponent))
                        throw new ApplicationParseException("Cells of the QuestionTable must be a QuestionComponent");

                    QuestionComponent questionComponent = (QuestionComponent) applicationComponent;

                    questionComponent.setName(questionComponent.getName() + "_" + (i+1));
                    questionComponent.setComponentId(questionComponent.getName()); // TODO this may not be correct
                    questionComponents.add(questionComponent);
                }

                parsedCells.put(column, new QuestionTableComponent.Cells(null, column, questionComponents));
            }
        }

        QuestionTableComponent.CellsMapping mapping1 = new QuestionTableComponent.CellsMapping(parsedCells);
        mapping1.setDatabaseId(mappingId);

        return mapping1;
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
        Map<String, Object> cells = (Map<String, Object>) map.get("cells");
        int numRows = (int) map.get("numRows");

        QuestionTableComponent.CellsMapping parsed = this.parseCellsMapping(numRows, cells);

        return new QuestionTableComponent(parsed, numRows);
    }
}
