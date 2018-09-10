package te.http.handling.deserialization.parsing;

import java.util.function.Predicate;

import io.vavr.control.Try;
import te.http.handling.exceptions.ParserConfigurationException;

/**
 * Represents a date format we support for converting strings to instances of type T.
 *
 * @see JavaDateParser
 * @see LocalDateParser
 */
public interface DateParser<T> {

    /**
     * @return a new instance of type T or an exception if that failed
     */
    T parseDateString(String dateString) throws Exception;

    /**
     * @return the date pattern we are trying to parse out of strings
     */
    String getPattern();

    /**
     * @return regular expression for testing whether a given string matches our date pattern
     */
    Predicate<String> getRegex();

    /**
     * @return new instance of type T parsed from 'dateString'
     * @throws ParserConfigurationException if a regular expression matches against a
     * string but then the associated parser cannot parse it
     */
    default T parse(String dateString) {
        return Try
                .of(() -> parseDateString(dateString))
                .getOrElseThrow((ex) ->
                        new ParserConfigurationException(ex, getPattern(), dateString)
                );
    }

    /**
     * @return whether or not a given string can be parsed by this date format
     */
    default boolean matches(String dateString) {
        return getRegex().test(dateString);
    }
}
