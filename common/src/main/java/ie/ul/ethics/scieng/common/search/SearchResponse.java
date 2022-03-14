package ie.ul.ethics.scieng.common.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * This class represents a common response that can be sent back from a search endpoint
 * @param <T> the type of the contained results
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class SearchResponse<T> {
    /**
     * The list of results
     */
    private List<T> results;
    /**
     * An optional error message to include
     */
    private String error;

    /**
     * Create a response from the given results
     * @param error an error message to include if an error occurred
     */
    public SearchResponse(List<T> results, String error) {
        this.results = results;
    }
}
