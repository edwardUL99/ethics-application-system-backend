package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This component represents a component that has options to choose from as the answer
 */
@Getter
@Setter
@Entity
public class SelectQuestionComponent extends QuestionComponent {
    /**
     * True if multiple answers are allowed
     */
    private boolean multiple;
    /**
     * The list of options for this question
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Option> options;

    /**
     * Create a default select question component
     */
    public SelectQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, false, new ArrayList<>());
    }

    /**
     * Create a SelectQuestionComponent
     * @param title the title of the question
     * @param name the name to give to the question
     * @param description the description of the question
     * @param required true if the question requires an answer, false if not
     * @param multiple true if multiple responses are allowed
     * @param options the list of options to add to the question
     */
    public SelectQuestionComponent(String title, String name, String description, boolean required, boolean multiple, List<Option> options) {
        super(ComponentType.SELECT_QUESTION, title, name, description, required);
        this.multiple = multiple;
        this.options = options;
    }

    /**
     * Clear the database ID of this component and also any child components
     */
    @Override
    public void clearDatabaseIDs() {
        this.databaseId = null;
        this.options.forEach(option -> option.setId(null));
    }

    /**
     * This class represents an option for the select question
     */
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    public static class Option {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        /**
         * The label for the option to display
         */
        private String label;
        /**
         * The value to send in the post request
         */
        private String value;
        /**
         * A uniwue UUID identifier to uniquely identify this option
         */
        private String identifier;

        /**
         * Create an Option where the value is the same as the label
         * @param value the value to use
         */
        public Option(String value) {
            this(value, value);
        }

        /**
         * Create an Option with the provided label and value
         * @param label the label value
         * @param value the value to send in the request
         */
        public Option(String label, String value) {
            this.label = label;
            this.value = value;
            this.identifier = UUID.randomUUID().toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Option option = (Option) o;
            return Objects.equals(id, option.id) && Objects.equals(label, option.label) && Objects.equals(value, option.value)
                    && Objects.equals(identifier, option.identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, label, value, identifier);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SelectQuestionComponent that = (SelectQuestionComponent) o;
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
