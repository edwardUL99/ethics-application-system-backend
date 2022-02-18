package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This component represents a question that has checkboxes that can have multiple answers
 */
@Entity
@Getter
public class CheckboxQuestionComponent extends SelectQuestionComponent {
    /**
     * Determines if the checkboxes should be rendered inline in a row
     */
    private boolean inline;

    /**
     * Create a default CheckboxQuestionComponent
     */
    public CheckboxQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, new ArrayList<>(), false);
    }

    /**
     * Create a CheckboxQuestionComponent
     * @param title the title of the component
     * @param name the name to give to the question
     * @param description question description
     * @param required true if required or not
     * @param options the options for the checkbox
     * @param inline if true, checkboxes will be rendered inline
     */
    public CheckboxQuestionComponent(String title, String name, String description, boolean required, List<Option> options,
                                     boolean inline) {
        super(title, name, description, required, true, options);
        setType(ComponentType.CHECKBOX_QUESTION);
        this.inline = inline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckboxQuestionComponent that = (CheckboxQuestionComponent) o;
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
