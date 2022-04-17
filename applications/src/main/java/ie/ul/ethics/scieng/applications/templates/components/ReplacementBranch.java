package ie.ul.ethics.scieng.applications.templates.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This branch represents a branch that results in a container being replaced by another container
 */
@Getter
@Setter
@Entity
public class ReplacementBranch extends Branch {
    /**
     * The list of replacements
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Replacement> replacements;

    /**
     * Create a default ReplacementBranch
     */
    public ReplacementBranch() {
        this(new ArrayList<>());
    }

    /**
     * Create a ReplacementBranch
     *
     * @param replacements the list of replacements if this branch is triggered
     */
    public ReplacementBranch(List<Replacement> replacements) {
        super(ComponentType.REPLACEMENT_BRANCH);
        this.replacements = replacements;
    }

    /**
     * This class represents a replacement of one container with another
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    public static class Replacement {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        /**
         * The container ID to replace
         */
        @Column(name = "replaceId")
        private String replace;
        /**
         * The ID of the container to add into the replacement. The ID can be [application-id]-[containerId]
         */
        private String target;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Replacement that = (Replacement) o;
            return Objects.equals(id, that.id) && Objects.equals(replace, that.replace) && Objects.equals(target, that.target);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, replace, target);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReplacementBranch that = (ReplacementBranch) o;
        return branchId != null && Objects.equals(branchId, that.branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
