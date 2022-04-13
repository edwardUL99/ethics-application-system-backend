package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.users.models.User;
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
     * If this answer has been created by another user than the applicant, this field is set
     */
    @OneToOne
    private User user;

    /**
     * Create an answer with the provided parameters
     * @param id the database ID of the answer
     * @param componentId the ID of the component the answer is attached to
     * @param value the value of the answer
     * @param valueType the type of the answer value
     */
    public Answer(Long id, String componentId, String value, ValueType valueType) {
        this(id, componentId, value, valueType, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(id, answer1.id) && Objects.equals(componentId, answer1.componentId) && Objects.equals(value, answer1.value)
                && Objects.equals(valueType, answer1.valueType) && Objects.equals(user, answer1.user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, componentId, value, valueType, user);
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
        OPTIONS,
        /**
         * An answer that is an image in base64 format
         */
        IMAGE
    }
}
