package ie.ul.edward.ethics.applications.parsing.converters;

import ie.ul.edward.ethics.applications.parsing.components.*;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This class represents a converter that can convert a CheckboxGroup
 */
public class CheckboxGroupConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("title", "defaultBranch", "checkboxes"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The checkbox group component is missing keys");

        if (!Map.class.isAssignableFrom(map.get("defaultBranch").getClass()))
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
        String type = (String)branch.get("type");

        if (ComponentTypes.ACTION_BRANCH.equals(type)) {
            return new ActionBranch((String)branch.get("action"), (String)branch.getOrDefault("reason", null));
        } else if (ComponentTypes.REPLACEMENT_BRANCH.equals(type)) {
            List<ReplacementBranch.Replacement> replacements = new ArrayList<>();

            for (Map<String, Object> replacement : (List<Map<String, Object>>)branch.get("replacements")) {
                String key = replacement.keySet().stream().findFirst().orElse(null);
                replacements.add(new ReplacementBranch.Replacement(key, (String)replacement.get(key)));
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

            checkboxes.add(new CheckboxGroupComponent.Checkbox(title, branch));
        }

        return new CheckboxGroupComponent((String)map.get("title"), defaultBranch, checkboxes);
    }
}
