package ie.ul.edward.ethics.applications.templates.components;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This component represents a question that has radios that can have only one answer
 */
@Entity
public class RadioQuestionComponent extends SelectQuestionComponent {
    /**
     * Create a default RadioQuestionComponent
     */
    public RadioQuestionComponent() {
        this(null, null, null, QuestionComponent.DEFAULT_REQUIRED, new ArrayList<>());
    }

    /**
     * Create a RadioQuestionComponent
     * @param title the title of the component
     * @param description the question description
     * @param name the name to give to the question
     * @param required true if required or not
     * @param options the options for the checkbox
     */
    public RadioQuestionComponent(String title, String name, String description, boolean required, List<SelectQuestionComponent.Option> options) {
        super(title, name, description, required, false, options, false);
        setType(ComponentType.RADIO_QUESTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RadioQuestionComponent that = (RadioQuestionComponent) o;
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
