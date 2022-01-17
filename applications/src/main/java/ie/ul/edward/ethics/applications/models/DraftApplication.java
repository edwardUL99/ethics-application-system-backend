package ie.ul.edward.ethics.applications.models;

import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

/**
 * This represents an application that is a draft
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DraftApplication {
    /**
     * The database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The application ID
     */
    private String applicationId;
    /**
     * The user that is creating this application
     */
    @OneToOne
    private User user;
    /**
     * The template of the application being filled in
     */
    @OneToOne(cascade = CascadeType.ALL)
    private ApplicationTemplate applicationTemplate;
    /**
     * The map of component IDs to the values (i.e. the answers)
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "values_mapping",
            joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "cells_id", referencedColumnName = "id")})
    @MapKey(name = "componentId")
    private Map<String, Value> valuesMap;

    /**
     * Ge
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    public static class Value {
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
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value value1 = (Value) o;
            return Objects.equals(id, value1.id) && Objects.equals(componentId, value1.componentId) && Objects.equals(value, value1.value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, componentId, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DraftApplication that = (DraftApplication) o;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
