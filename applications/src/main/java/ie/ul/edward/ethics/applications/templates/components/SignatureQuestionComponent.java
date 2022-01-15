package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a question that requires a signature. Signatures are always required, regardless of a required
 * value provided, it will be overridden by.
 * A signature component always provides a field for the date it was signed with name `name-date`
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class SignatureQuestionComponent extends QuestionComponent {
    /**
     * The identifier of who is expected to sign
     *
     */
    private String label;
    /**
     * Create a default SignatureQuestionConverter
     */
    public SignatureQuestionComponent() {
        this(null, null, null, null);
    }

    /**
     * Create a SignatureQuestionConverter
     * @param title the title of the signature component
     * @param name the name to give to the question
     * @param description the description of the component
     * @param label the label identifying who is supposed to sign it
     */
    public SignatureQuestionComponent(String title, String name, String description, String label) {
        super(ComponentTypes.SIGNATURE, title, name, description, true); //  a signature will always be required if present
        this.label = label;
    }

    /**
     * Overridden to override any value of required and always set it to true
     * @param required this value will be ignored and overridden with true
     */
    public void setRequired(boolean required) {
        this.required = true;
    }
}
