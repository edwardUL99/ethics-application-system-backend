package ie.ul.ethics.scieng.common.search;

import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class allows the building of a combination of specifications
 * @param <T> the type being searched
 * @param <S> the type of the searchable specification
 */
public class SpecificationBuilder<T, S extends SearchSpecification<T>> {
    /**
     * The list of criteria to combine
     */
    private final List<SearchCriteria> criteria;
    /**
     * The class of the searchable specification
     */
    private final Class<S> cls;
    /**
     * Determines if multiple variables should be OR'd together instead of the default and
     */
    private boolean or;

    /**
     * Create a builder instance
     * @param cls the class of the searchable specification
     */
    public SpecificationBuilder(Class<S> cls) {
        this.criteria = new ArrayList<>();
        this.cls = cls;
    }

    /**
     * Set the value of or
     * @param or if true, multiple criteria will be or'd together, else and'd
     * @return the builder instance for chaining
     */
    public SpecificationBuilder<T, S> or(boolean or) {
        this.or = or;
        return this;
    }

    /**
     * Create a search criteria and add it to the specification builder
     * @param key the key for the search
     * @param operation the search operation
     * @param value the search value
     * @return the builder instance for chaining
     */
    public SpecificationBuilder<T, S> with(String key, String operation, Object value) {
        this.criteria.add(new SearchCriteria(key, operation, value, or));

        return this;
    }

    /**
     * Build the specification
     * @return the built specification
     */
    public Specification<T> build() {
        if (criteria.size() == 0)
            return null;

        try {
            Constructor<?> constructor = cls.getDeclaredConstructor(SearchCriteria.class);

            List<SearchSpecification<T>> specs = criteria.stream()
                    .map(c -> {
                        try {
                            return cls.cast(constructor.newInstance(c));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (specs.size() == 0)
                return null;

            Specification<T> result = specs.get(0);

            for (int i = 1; i < specs.size(); i++) {
                result = (criteria.get(i).isOrPredicate()) ?
                        Specification.where(result)
                                        .or(specs.get(i))
                        : Specification.where(result)
                                        .and(specs.get(i));
            }

            return result;
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("The class " + cls + " does not have a constructor that takes a single SearchCriteria object");
        }
    }
}
