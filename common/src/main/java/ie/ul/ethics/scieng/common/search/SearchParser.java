package ie.ul.ethics.scieng.common.search;

import org.springframework.data.jpa.domain.Specification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses a search string into the built specification
 * @param <T> the type being searched
 * @param <S> the specification being searched
 */
public class SearchParser<T, S extends SearchSpecification<T>> {
    /**
     * The builder to build the specification with
     */
    private final SpecificationBuilder<T, S> builder;

    /**
     * Build a search parser instance
     * @param specificationClass the class object of the specification instance
     */
    public SearchParser(Class<S> specificationClass) {
        this.builder = new SpecificationBuilder<>(specificationClass);
    }

    /**
     * Parse the search string into a specification
     * @param search the search string to parse
     * @param operationsPattern the pattern defining how the search string is formatted
     * @return the parsed specification or null if an error occurred
     */
    public Specification<T> parse(String search, String operationsPattern) {
        return this.parse(search, operationsPattern, false);
    }

    /**
     * Parse the search string into a specification
     * @param search the search string to parse
     * @param operationsPattern the pattern defining how the search string is formatted
     * @param or determines if multiple results should be OR'd instead of AND'd
     * @return the parsed specification or null if an error occurred
     */
    public Specification<T> parse(String search, String operationsPattern, boolean or) {
        Pattern pattern = Pattern.compile(operationsPattern);
        Matcher matcher = pattern.matcher(search + ",");

        builder.or(or);

        while (matcher.find())
           builder.with(matcher.group(1), matcher.group(2), matcher.group(3));

        return builder.build();
    }
}
