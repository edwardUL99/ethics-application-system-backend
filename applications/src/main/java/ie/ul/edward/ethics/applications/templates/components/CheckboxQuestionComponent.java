package ie.ul.edward.ethics.applications.templates.components;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This component represents a question that has checkboxes that can have multiple answers
 */
@Entity
public class CheckboxQuestionComponent extends SelectQuestionComponent {
    /**
     * Create a default CheckboxQuestionComponent
     */
    public CheckboxQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, new ArrayList<>());
    }

    /**
     * Create a CheckboxQuestionComponent
     * @param title the title of the component
     * @param name the name to give to the question
     * @param description question description
     * @param required true if required or not
     * @param options the options for the checkbox
     */
    public CheckboxQuestionComponent(String title, String name, String description, boolean required, List<Option> options) {
        super(title, name, description, required, true, options, false);
        setType(ComponentType.CHECKBOX_QUESTION);
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
