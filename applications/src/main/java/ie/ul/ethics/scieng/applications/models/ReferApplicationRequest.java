package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This class represents a request to refer an application
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReferApplicationRequest {
    /**
     * The application id (not dbId) of the application to refer
     */
    @NotNull
    private String id;
    /**
     * The list of editable field IDs
     */
    @NotNull
    private List<String> editableFields;
    /**
     * The username of the referrer username
     */
    @NotNull
    private String referrer;
}
