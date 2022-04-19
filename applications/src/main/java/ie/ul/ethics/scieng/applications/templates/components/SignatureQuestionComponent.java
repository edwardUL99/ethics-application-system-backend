package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * This class represents a question that requires a signature. Signatures are always required, regardless of a required
 * value provided, it will be overridden by.
 * A signature component always provides a field for the date it was signed with name `name-date`
 */
@Getter
@Setter
@Entity
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
        this(null, null, null, null, true);
    }

    /**
     * Create a SignatureQuestionConverter
     * @param title the title of the signature component
     * @param name the name to give to the question
     * @param description the description of the component
     * @param label the label identifying who is supposed to sign it
     * @param required determines if the signature is required
     */
    public SignatureQuestionComponent(String title, String name, String description, String label, boolean required) {
        super(ComponentType.SIGNATURE, title, name, description, required);
        this.label = label;
    }

    /**
     * Overridden to override any value of required and always set it to true
     * @param required this value will be ignored and overridden with true
     */
    public void setRequired(boolean required) {
        this.required = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SignatureQuestionComponent that = (SignatureQuestionComponent) o;
        return databaseId != null && Objects.equals(databaseId, that.databaseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
