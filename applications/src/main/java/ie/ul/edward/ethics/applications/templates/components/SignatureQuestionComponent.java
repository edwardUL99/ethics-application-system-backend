package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;

/**
 * This class represents a question that requires a signature. Signatures are always required, regardless of a required
 * value provided, it will be overridden by
 */
@EqualsAndHashCode(callSuper = false)
public class SignatureQuestionComponent extends QuestionComponent {
    /**
     * Create a default SignatureQuestionConverter
     */
    public SignatureQuestionComponent() {
        this(null, null);
    }

    /**
     * Create a SignatureQuestionConverter
     * @param title the title of the signature component
     * @param description the description of the component
     */
    public SignatureQuestionComponent(String title, String description) {
        super(ComponentTypes.SIGNATURE, title, description, true); //  a signature will always be required if present
    }

    /**
     * Overridden to override any value of required and always set it to true
     * @param required this value will be ignored and overridden with true
     */
    public void setRequired(boolean required) {
        this.required = true;
    }
}
