package ie.ul.ethics.scieng.common.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This interface represents a common interface that any controller allowing search operations should
 * implement
 * @param <T> the type of the results
 */
public interface SearchController<T> {
    /**
     * This is the search endpoint that takes a query and true to OR multiple criteria
     * @param query the query to search
     * @param or true to OR multiple criteria (false is AND)
     * @return the response body
     */
    @GetMapping("/search")
    ResponseEntity<SearchResponse<T>> search(@RequestParam String query, @RequestParam(required = false) boolean or);
}
