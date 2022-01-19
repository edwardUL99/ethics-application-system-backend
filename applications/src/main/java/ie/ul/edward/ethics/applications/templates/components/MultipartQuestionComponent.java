package ie.ul.edward.ethics.applications.templates.components;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

/**
 * This component represents a question that has multiple parts and they may be conditional
 */
@Getter
@Setter
@Entity
public class MultipartQuestionComponent extends QuestionComponent {
    /**
     * Determines if this question is conditional. If not, all branches are disabled and all question parts shown regardless
     * of a condition being met
     */
    private boolean conditional;
    /**
     * The mapping of part ID/number to the QuestionPart object
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "part_names_mapping",
            joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "databaseId")},
            inverseJoinColumns = {@JoinColumn(name = "parts_id", referencedColumnName = "id")})
    @MapKey(name = "partName")
    private Map<String, QuestionPart> parts;

    /**
     * Create a default MultipartQuestionComponent
     */
    public MultipartQuestionComponent() {
        this(null, DEFAULT_REQUIRED,true, new HashMap<>());
    }

    /**
     * Create a MultipartQuestionComponent
     * @param title the title of the question, default null
     * @param required true if required, false if not
     * @param conditional true if its conditional, false if not
     * @param parts the map of part IDs to parts
     */
    public MultipartQuestionComponent(String title, boolean required, boolean conditional, Map<String, QuestionPart> parts) {
        super(ComponentType.MULTIPART_QUESTION, title, null, null, required);
        this.conditional = conditional;
        this.parts = parts;
    }

    /**
     * This class represents a question part
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Entity
    public static class QuestionPart {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        /**
         * The name of the part
         */
        private String partName;
        /**
         * The question component that represents the question
         */
        @OneToOne(cascade = CascadeType.ALL)
        private QuestionComponent question;
        /**
         * The list of branches from this part
         */
        @ManyToMany(cascade = CascadeType.ALL)
        private List<QuestionBranch> branches = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QuestionPart that = (QuestionPart) o;
            return Objects.equals(id, that.id) && Objects.equals(question, that.question) && Objects.equals(branches, that.branches);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, question, branches);
        }
    }

    /**
     * This class represents a branch that is triggered based on the condition of another question
     */
    @Getter
    @Setter
    @Entity
    public static class QuestionBranch extends Branch {
        /**
         * The part of the question to branch to
         */
        private String part;
        /**
         * The value of the current question that results in the branch being triggered
         */
        private String value;

        /**
         * Create a default QuestionBranch
         */
        public QuestionBranch() {
            this(null, null);
        }

        /**
         * Create a QuestionBranch component
         * @param part the name of the part the branch should branch to
         * @param value the value of the current part that determines part should be branched to
         */
        public QuestionBranch(String part, String value) {
            super(ComponentType.QUESTION_BRANCH);
            this.part = part;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QuestionBranch that = (QuestionBranch) o;
            return Objects.equals(part, that.part) && Objects.equals(value, that.value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(part, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MultipartQuestionComponent that = (MultipartQuestionComponent) o;
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
