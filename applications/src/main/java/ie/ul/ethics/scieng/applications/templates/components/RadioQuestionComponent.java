package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This component represents a question that has radios that can have only one answer
 */
@Entity
@Getter
@Setter
public class RadioQuestionComponent extends SelectQuestionComponent {
    /**
     * Determines if the radios should be rendered inline in a row
     */
    private boolean inline;

    /**
     * Create a default RadioQuestionComponent
     */
    public RadioQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, new ArrayList<>(), false);
    }

    /**
     * Create a RadioQuestionComponent
     * @param title the title of the component
     * @param description the question description
     * @param name the name to give to the question
     * @param required true if required or not
     * @param options the options for the checkbox
     * @param inline determine if radios should be rendered inline
     */
    public RadioQuestionComponent(String title, String name, String description, boolean required, List<SelectQuestionComponent.Option> options,
                                  boolean inline) {
        super(title, name, description, required, false, options);
        setType(ComponentType.RADIO_QUESTION);
        this.inline = inline;
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
