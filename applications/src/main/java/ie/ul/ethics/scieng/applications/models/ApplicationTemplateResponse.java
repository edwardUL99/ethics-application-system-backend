package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a response to a request to retrieve all parsed application templates
 */
@Getter
@EqualsAndHashCode
public class ApplicationTemplateResponse {
    /**
     * The map of application IDs to the applications
     */
    private final Map<String, ApplicationTemplate> applications;

    /**
     * Construct a response from the array of templates
     * @param templates the array of loaded application templates
     */
    public ApplicationTemplateResponse(ApplicationTemplate[] templates) {
        this.applications = Arrays.stream(templates)
                .collect(Collectors.toMap(
                        ApplicationTemplate::getId,
                        t -> t
                ));
    }
}
