package ie.ul.edward.ethics.applications.models.applications;

import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

/**
 * This represents an application that is a draft
 */
@Entity
@Getter
@Setter
public class DraftApplication extends Application {
    /**
     * The template of the application being filled in
     */
    @OneToOne
    private ApplicationTemplate applicationTemplate;
    /**
     * The map of component IDs to the values (i.e. the answers)
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "values_mapping",
            joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "cells_id", referencedColumnName = "id")})
    @MapKey(name = "componentId")
    private Map<String, Value> values;

    /**
     * Create a default DraftApplication
     */
    public DraftApplication() {
        this(null, null, null, null, new HashMap<>());
    }

    /**
     * Create a DraftApplication
     * @param id the database ID of the object
     * @param applicationId the ethics committee application ID
     * @param user the user that owns this application
     * @param applicationTemplate the template the application is being created to
     * @param values the map of component IDs to the values object
     */
    public DraftApplication(Long id, String applicationId, User user, ApplicationTemplate applicationTemplate, Map<String, Value> values) {
        super(id, applicationId, user, ApplicationStatus.DRAFT);
        this.applicationTemplate = applicationTemplate;
        this.values = values;
    }

    /**
     * Overridden as you cannot change the status of a DraftApplication
     * This operation is a no-op
     */
    @Override
    public void setStatus(ApplicationStatus status) {
        this.status = ApplicationStatus.DRAFT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DraftApplication that = (DraftApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(applicationId, that.applicationId) && Objects.equals(user, that.user)
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(values, that.values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, applicationTemplate, values);
    }

    /**
     * This class represents the value of an answer given on a form
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
}
