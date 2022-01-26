package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.*;

import java.util.*;

/**
 * This class represents a converter that can convert a CheckboxGroup
 */
@Converter(ComponentType.CHECKBOX_GROUP)
public class CheckboxGroupConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.CHECKBOX_GROUP, map.keySet(), "title", "defaultBranch", "checkboxes");

        Object defaultBranch = map.get("defaultBranch");

        if (defaultBranch != null && !Map.class.isAssignableFrom(defaultBranch.getClass()))
            throw new ApplicationParseException("The defaultBranch field must be a map");

        if (!List.class.isAssignableFrom(map.get("checkboxes").getClass()))
            throw new ApplicationParseException("The options field must map to a List");
    }

    /**
     * Parse the branch map
     * @param branch the map representing the branch
     * @return the parsed branch
     */
    @SuppressWarnings("unchecked")
    private Branch parseBranch(Map<String, Object> branch) {
        if (branch == null)
            return null;

        ComponentType type = ComponentType.of((String)branch.get("type"));

        if (ComponentType.ACTION_BRANCH.equals(type)) {
            return new ActionBranch((String)branch.get("action"), (String)branch.getOrDefault("comment", null));
        } else if (ComponentType.REPLACEMENT_BRANCH.equals(type)) {
            List<ReplacementBranch.Replacement> replacements = new ArrayList<>();

            for (Map<String, Object> replacement : (List<Map<String, Object>>)branch.get("replacements")) {
                String key = replacement.keySet().stream().findFirst().orElse(null);
                replacements.add(new ReplacementBranch.Replacement(null, key, (String)replacement.get(key)));
            }

            return new ReplacementBranch(replacements);
        } else {
            throw new ApplicationParseException("Illegal branch type: " + type);
        }
    }

    /**
     * Convert the provided map to the equivalent ApplicationComponent.
     * Should call the validate method to ensure the map is valid
     *
     * @param map the map to convert
     * @return the equivalent application component
     * @throws ApplicationParseException if the map isn't valid or an error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);
        Branch defaultBranch = parseBranch((Map<String, Object>)map.get("defaultBranch"));
        List<CheckboxGroupComponent.Checkbox> checkboxes = new ArrayList<>();

        for (Map<String, Object> checkbox : (List<Map<String, Object>>)map.get("checkboxes")) {
            String title = (String)checkbox.get("title");
            Branch branch = (checkbox.containsKey("branch")) ? parseBranch((Map<String, Object>) checkbox.get("branch")):null;

            checkboxes.add(new CheckboxGroupComponent.Checkbox(null, title, branch));
        }

        return new CheckboxGroupComponent((String)map.get("title"), defaultBranch, checkboxes, (boolean)map.getOrDefault("multiple", false));
    }
}
