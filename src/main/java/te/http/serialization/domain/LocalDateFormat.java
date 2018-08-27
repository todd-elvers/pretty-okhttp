package te.http.serialization.domain;

import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This represents a {@link java.time.LocalDate} format we support deserialization from.
 *
 * More specifically this captures a pattern to convert {@link String}s to
 * {@link java.time.LocalDate}s, a regex to identify if a given string matches the
 * pattern, and a formatter capable of parsing {@link String}s of that pattern.
 */
public class LocalDateFormat {
    private String pattern;
    private DateTimeFormatter formatter;
    private Predicate<String> regex;

    public LocalDateFormat(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

    public boolean matches(String dateString) {
        return regex.test(dateString);
    }
}
