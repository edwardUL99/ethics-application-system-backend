package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.users.models.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * This class represents a ReferApplicationRequest that has been mapped
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MappedReferApplicationRequest {
    /**
     * The loaded application
     */
    private Application application;
    /**
     * The list of field IDs that can be edited
     */
    private List<String> editableFields;
    /**
     * The loaded referrer user object
     */
    private User referrer;
}
