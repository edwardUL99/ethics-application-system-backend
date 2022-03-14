package ie.ul.ethics.scieng.common.search;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class allows the specification of a key, operation and the value to search based on.
 * A common class that can be used in any search
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    /**
     * The search key
     */
    private String key;
    /**
     * The search operation
     */
    private String operation;
    /**
     * The search value to search with
     */
    private Object value;
    /**
     * Determines if the specification is to be OR'd
     */
    private boolean orPredicate;
}
