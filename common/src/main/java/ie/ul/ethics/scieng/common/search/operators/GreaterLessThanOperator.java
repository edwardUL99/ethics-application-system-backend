package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * An operator that can implement < or >
 */
public class GreaterLessThanOperator extends BaseSearchOperator {
    /**
     * If true, the operator is >, else <
     */
    protected final boolean greaterThan;

    /**
     * Construct the operator
     *
     * @param greaterThan true if >, else <
     */
    protected GreaterLessThanOperator(boolean greaterThan) {
        super((greaterThan) ? ">":"<");
        this.greaterThan = greaterThan;
    }

    /**
     * Attempts to parse the value into a local date time
     * @param value the string value
     * @param iso true to parse iso, false to parse yyy-MM-dd
     * @return the parsed value, null if failed to parse
     */
    private LocalDateTime parseDateString(String value, boolean iso) {
        try {
            LocalDateTime parsed;

            if (iso) {
                parsed = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                parsed = localDate.atStartOfDay();
            }

            return parsed;
        } catch (DateTimeParseException ex) {
            if (iso) {
                // failed iso, so try yyyy-MM-dd
                return parseDateString(value, false);
            } else {
                return null;
            }
        }
    }

    /**
     * Parse the value into a LocalDateTime
     * @param value the value of the criteria
     * @return the parsed date time
     */
    private LocalDateTime parseLocalDateTime(Object value) {
        LocalDateTime parsed;

        if (value instanceof LocalDateTime) {
            parsed = (LocalDateTime) value;
        } else if (value instanceof String) {
            parsed = parseDateString((String)value, true);
        } else {
            return null;
        }

        return parsed;
    }

    /**
     * Perform the operation on the root value
     *
     * @param rootValue       the value on the entity
     * @param criteriaBuilder the builder used to build the predicate
     * @param criteria        the search criteria
     * @return the built predicate
     */
    @Override
    @SuppressWarnings("unchecked")
    public Predicate operate(Expression<?> rootValue, CriteriaBuilder criteriaBuilder, SearchCriteria criteria) {
        Class<?> cls = rootValue.getJavaType();

        if (cls == LocalDateTime.class) {
            Object value = criteria.getValue();
            LocalDateTime parsed = parseLocalDateTime(value);

            if (parsed == null)
                return null;

            return (this.greaterThan) ? criteriaBuilder.greaterThanOrEqualTo((Expression<LocalDateTime>)rootValue, parsed)
                : criteriaBuilder.lessThanOrEqualTo((Expression<LocalDateTime>)rootValue, parsed);
        } else {
            return criteriaBuilder.lessThanOrEqualTo(
                    (Expression<String>) rootValue, criteria.getValue().toString());
        }
    }
}
