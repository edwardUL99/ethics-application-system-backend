package ie.ul.ethics.scieng.applications.models.applications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 * This class represents the value of an answer given on a form
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    /**
     * The database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The ID of the component
     */
    private String componentId;
    /**
     * The value, i.e. answer
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String value;
    /**
     * The type of the value
     */
    private ValueType valueType;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(id, answer1.id) && Objects.equals(componentId, answer1.componentId) && Objects.equals(value, answer1.value)
                && Objects.equals(valueType, answer1.valueType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, componentId, value, valueType);
    }

    /**
     * This enum represents the type of the value
     */
    public enum ValueType {
        /**
         * A text answer
         */
        TEXT,
        /**
         * A number answer
         */
        NUMBER,
        /**
         * An answer that is one or more options selected (stored in this class as a comma-separated string
         */
        OPTIONS
    }
}
