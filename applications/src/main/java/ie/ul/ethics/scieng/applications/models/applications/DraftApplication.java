package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.models.User;
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
     * TODO this may be relevant in Application class as it may be needed for submitted applications too. If moved, you may not need createDrafTApplication with update parameter, the logic of that (and update parameter) may be moved into createApplication
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
     * This method determines if the provided user can view this application
     *
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    @Override
    public boolean canBeViewedBy(User user) {
        return this.user.getUsername().equals(user.getUsername());
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
            Value value1 = (Value) o;
            return Objects.equals(id, value1.id) && Objects.equals(componentId, value1.componentId) && Objects.equals(value, value1.value)
                    && Objects.equals(valueType, value1.valueType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, componentId, value, valueType);
        }
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
