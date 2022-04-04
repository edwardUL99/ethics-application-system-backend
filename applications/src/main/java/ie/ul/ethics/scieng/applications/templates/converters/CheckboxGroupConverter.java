package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.*;

import java.util.*;

/**
 * This class represents a converter that can convert a CheckboxGroup
 */
@Converter(ComponentType.CHECKBOX_GROUP)
public class CheckboxGroupConverter extends BaseConverter {
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
            throw new ApplicationParseException("The checkboxes field must map to a list");
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

        Branch parsed;

        if (ComponentType.ACTION_BRANCH.equals(type)) {
            parsed = new ActionBranch((String)branch.get("action"), (String)branch.getOrDefault("comment", null));
        } else if (ComponentType.REPLACEMENT_BRANCH.equals(type)) {
            List<ReplacementBranch.Replacement> replacements = new ArrayList<>();

            for (Map<String, Object> replacement : (List<Map<String, Object>>)branch.get("replacements")) {
                replacements.add(new ReplacementBranch.Replacement(ComponentConverter.parseDatabaseId(replacement.getOrDefault("id", null)),
                        (String)replacement.get("replace"), (String)replacement.get("target")));
            }

            parsed = new ReplacementBranch(replacements);
        } else {
            throw new ApplicationParseException("Illegal branch type: " + type);
        }

        parsed.setBranchId(ComponentConverter.parseDatabaseId(branch.getOrDefault("branchId", null)));

        return parsed;
    }

    /**
     * Create the base component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ApplicationComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        Branch defaultBranch = parseBranch((Map<String, Object>)map.get("defaultBranch"));
        List<CheckboxGroupComponent.Checkbox> checkboxes = new ArrayList<>();

        for (Map<String, Object> checkbox : (List<Map<String, Object>>)map.get("checkboxes")) {
            String title = (String)checkbox.get("title");
            Branch branch = (checkbox.containsKey("branch")) ? parseBranch((Map<String, Object>) checkbox.get("branch")):null;

            CheckboxGroupComponent.Checkbox box = new CheckboxGroupComponent.Checkbox(ComponentConverter.parseDatabaseId(checkbox.getOrDefault("id", null)), title, branch);

            String identifier = (String) checkbox.get("identifier");

            if (identifier != null) {
                box.setIdentifier(identifier);
            }

            checkboxes.add(box);
        }

        return new CheckboxGroupComponent((String)map.get("title"), defaultBranch, checkboxes, (boolean)map.getOrDefault("multiple", false),
                (boolean)map.getOrDefault("required", false));
    }
}
