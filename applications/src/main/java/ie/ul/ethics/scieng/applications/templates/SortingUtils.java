package ie.ul.ethics.scieng.applications.templates;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.CompositeComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides utilities for sorting components
 */
public final class SortingUtils {
    /**
     * Recursively find components to sort and add them to the toSort list. Does not perform the sorting however
     * @param component the current component to sort
     * @param toSort the list to sort
     */
    public static void findSortRecursive(ApplicationComponent component, List<ApplicationComponent> toSort) {
        if (component.isComposite()) {
            CompositeComponent composite = (CompositeComponent) component;
            toSort.add(composite);

            for (ApplicationComponent child: composite.getComponents()) {
                findSortRecursive(child, toSort);
            }
        } else {
            toSort.add(component);
        }
    }

    /**
     * Sort all the composite components in the provided list
     * @param sort the list of components to sort. Clears the list after all the contained elements are sorted
     */
    public static void sort(List<ApplicationComponent> sort) {
        sort.forEach(ApplicationComponent::sortComponents);
        sort.clear();
    }

    /**
     * Sort the list of components provided
     * @param components the components to sort
     */
    public static void sortComponents(List<ApplicationComponent> components) {
        List<ApplicationComponent> toSort = new ArrayList<>();

        for (ApplicationComponent child : components) {
            findSortRecursive(child, toSort);
            sort(toSort);
        }

        Collections.sort(components);
    }
}
