package ie.ul.edward.ethics.applications.templates.components;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This component represents a question that has multiple parts and they may be conditional
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class MultipartQuestionComponent extends ApplicationComponent {
    /**
     * Determines if this question is conditional. If not, all branches are disabled and all question parts shown regardless
     * of a condition being met
     */
    private boolean conditional;
    /**
     * The mapping of part ID/number to the QuestionPart object
     */
    private Map<String, QuestionPart> parts;

    /**
     * Create a default MultipartQuestionComponent
     */
    public MultipartQuestionComponent() {
        this(true, new HashMap<>());
    }

    /**
     * Create a MultipartQuestionBranch
     * @param conditional true if its conditional, false if not
     * @param parts the map of part IDs to parts
     */
    public MultipartQuestionComponent(boolean conditional, Map<String, QuestionPart> parts) {
        super(ComponentTypes.MULTIPART_QUESTION, null);
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
    @EqualsAndHashCode
    public static class QuestionPart {
        /**
         * The question component that represents the question
         */
        private QuestionComponent question;
        /**
         * The list of branches from this part
         */
        private List<QuestionBranch> branches = new ArrayList<>();
    }

    /**
     * This class represents a branch that is triggered based on the condition of another question
     */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
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

        public QuestionBranch(String part, String value) {
            super(ComponentTypes.QUESTION_BRANCH);
            this.part = part;
            this.value = value;
        }
    }
}
