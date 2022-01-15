package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A QuestionComponent represents a component that an answer is provided for it.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class QuestionComponent extends ApplicationComponent {
    /**
     * The description of the component
     */
    protected String description;
    /**
     * Determines if this question is required or not
     */
    protected boolean required;

    /**
     * Create a default QuestionComponent
     */
    public QuestionComponent() {
        this(null, null, null, false);
    }

    /**
     * Create a QuestionComponent
     * @param type the type of the component
     * @param title the title of the question
     * @param description the description of the question
     * @param required true if an answer is required, false if not
     */
    public QuestionComponent(String type, String title, String description, boolean required) {
        super(type, title);
        this.description = description;
        this.required = required;
    }
}
