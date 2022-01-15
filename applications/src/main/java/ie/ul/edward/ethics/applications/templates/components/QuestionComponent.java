package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A QuestionComponent represents a component that an answer is provided for it.
 * A question is always composed of itself (even if it might have sub-component questions, it is not considered a recursive
 * definition, since it shouldn't be able to store any other sub-components extending CompositeComponent
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class QuestionComponent extends SimpleComponent {
    /**
     * The description of the component
     */
    protected String description;
    /**
     * The name of the question
     */
    protected String name;
    /**
     * Determines if this question is required or not
     */
    protected boolean required;

    /**
     * The default value for required
     */
    public static boolean DEFAULT_REQUIRED = true;

    /**
     * Create a default QuestionComponent
     */
    public QuestionComponent() {
        this(null, null, null, null, DEFAULT_REQUIRED);
    }

    /**
     * Create a QuestionComponent
     * @param type the type of the component
     * @param title the title of the question
     * @param description the description of the question
     * @param name the name to give to the question
     * @param required true if an answer is required, false if not
     */
    public QuestionComponent(String type, String title, String description, String name, boolean required) {
        super(type, title);
        this.name = name;
        this.description = description;
        this.required = required;
    }
}
