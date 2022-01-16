package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * This component represents a table where the columns are the name of the questions being asked and the rows are rows of
 * inputs to answer these questions
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class QuestionTableComponent extends SimpleComponent {
    /**
     * This map provides the mapping of the column names to the question components
     */
    private Map<String, List<QuestionComponent>> columns;

    /**
     * Create a QuestionTableComponent
     * @param columns the columns for the component
     */
    public QuestionTableComponent(Map<String, List<QuestionComponent>> columns) {
        super(ComponentTypes.QUESTION_TABLE, null);
        this.columns = columns;
    }
}
