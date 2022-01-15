package ie.ul.edward.ethics.applications.templates.components;

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
     * The type of question that gets passed into the input type attribute, e.g. text, email
     */
    protected String questionType;

    /**
     * Create a default TextQuestionComponent
     */
    public TextQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, false, "text");
    }

    /**
     * Create a TextQuestionComponent
     * @param title the title of the question
     * @param name the name to give to the question
     * @param description a description for the question
     * @param required true if the question requires an answer, false if not
     * @param singleLine true if the answer is single line, false if multi-line
     * @param questionType the type of input, e.g. email, text, password passed to HTML input type attribute
     */
    public TextQuestionComponent(String title, String name, String description, boolean required, boolean singleLine, String questionType) {
        super(ComponentTypes.TEXT_QUESTION, title, name, description, required);
        this.singleLine = singleLine;
        this.questionType = questionType;
    }
}
