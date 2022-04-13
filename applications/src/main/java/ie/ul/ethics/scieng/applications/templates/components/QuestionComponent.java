package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * A QuestionComponent represents a component that an answer is provided for it.
 * A question is always composed of itself (even if it might have sub-component questions, it is not considered a recursive
 * definition, since it shouldn't be able to store any other sub-components extending CompositeComponent
 */
@Getter
@Setter
@Entity
public abstract class QuestionComponent extends SimpleComponent {
    /**
     * The description of the component
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
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
     * Determines if the component is editable or not
     */
    protected boolean editable;
    /**
     * The specified string to autofill the field from
     */
    protected String autofill;
    /**
     * Determines if this field is a candidate for input from a supervisor and prompt the applicant to request that
     */
    protected Boolean requestInput;

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
     * @param name the name of the component
     * @param description the description of the question
     * @param required true if an answer is required, false if not
     */
    public QuestionComponent(ComponentType type, String title, String name, String description, boolean required) {
        super(type, title);
        this.name = name;
        this.description = description;
        this.required = required;
        this.editable = true;
        this.autofill = "";
    }
}
