package ie.ul.edward.ethics.applications.parsing.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This component represents a question that allows a text answer
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class TextQuestionComponent extends QuestionComponent {
    /**
     * True if a single line is allowed for the question, false for multiple line
     */
    private boolean singleLine;

    /**
     * Create a default TextQuestionComponent
     */
    public TextQuestionComponent() {
        this(null, null, false, false);
    }

    /**
     * Create a TextQuestionComponent
     * @param title the title of the question
     * @param description a description for the question
     * @param required true if the question requires an answer, false if not
     * @param singleLine true if the answer is single line, false if multi-line
     */
    public TextQuestionComponent(String title, String description, boolean required, boolean singleLine) {
        super(ComponentTypes.TEXT_QUESTION, title, description, required);
        this.singleLine = singleLine;
    }
}
