package te.http.handling.deserialization.domain;

import java.util.function.Predicate;

/**
 * Represents a format we support for converting strings to instances of type T.
 *
 * @see DateFormat
 * @see LocalDateFormat
 */
public interface Format<T> {

    /**
     * @return a date from a string, throwing a {@link RuntimeException} if the parsing fails.
     */
    T parse(String dateString);

    /**
     * @return the date pattern we are trying to parse out of strings
     */
    String getPattern();

    /**
     * @return regular expression for testing whether a given string matches our date pattern
     */
    Predicate<String> getRegex();

    default boolean matches(String dateString) {
        return getRegex().test(dateString);
    }
}
