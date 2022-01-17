package ie.ul.edward.ethics.applications.templates.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

/**
 * This component represents a table where the columns are the name of the questions being asked and the rows are rows of
 * inputs to answer these questions
 */
@Getter
@Setter
@Entity
public class QuestionTableComponent extends SimpleComponent {
    /**
     * This map provides the mapping of the column names to the question components
     */
    @OneToOne(cascade = CascadeType.ALL)
    private CellsMapping columns;

    /**
     * Create a default QuestionTableComponent
     */
    public QuestionTableComponent() {
        this(new HashMap<>());
    }

    /**
     * Create a QuestionTableComponent
     * @param columns the columns for the component
     */
    public QuestionTableComponent(Map<String, Cells> columns) {
        super(ComponentTypes.QUESTION_TABLE, null);
        this.columns = new CellsMapping(columns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuestionTableComponent that = (QuestionTableComponent) o;
        return databaseId != null && Objects.equals(databaseId, that.databaseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * This class represents a list of cells for a column
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Entity
    public static class Cells {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long databaseId;
        /**
         * The name of the column owning these cells
         */
        private String columnName;
        /**
         * The list of components for this cell
         */
        @ManyToMany(cascade = CascadeType.ALL)
        private List<QuestionComponent> components;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cells cells = (Cells) o;
            return Objects.equals(components, cells.components) && Objects.equals(databaseId, cells.databaseId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(components, databaseId);
        }
    }

    /**
     * A mapping of column names to its cells
     */
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    public static class CellsMapping {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long databaseId;
        /**
         * The mapping of column names to its cells
         */
        @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinTable(name = "column_names_mapping",
                joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "databaseId")},
                inverseJoinColumns = {@JoinColumn(name = "cells_id", referencedColumnName = "databaseId")})
        @MapKey(name = "columnName")
        private Map<String, Cells> columns = new HashMap<>();

        /**
         * Create a CellsMapping
         * @param columns the mapping of cells
         */
        public CellsMapping(Map<String, Cells> columns) {
            this.columns = columns;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CellsMapping that = (CellsMapping) o;
            return Objects.equals(columns, that.columns) && Objects.equals(databaseId, that.databaseId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(columns, databaseId);
        }
    }
}
