package te.http.serialization.domain;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This represents a {@link java.util.Date} format we support deserialization from.
 *
 * More specifically this captures a pattern to convert {@link String}s to
 * {@link java.util.Date}s, a regex to identify if a given string matches the
 * pattern, and a formatter capable of parsing {@link String}s of that pattern.
 */
public class DateFormat {
    private String pattern;
    private FastDateFormat formatter;
    private Predicate<String> regex;

    public DateFormat(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = FastDateFormat.getInstance(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    public FastDateFormat getFormatter() {
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
