package te.http.handling.deserialization.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.control.Try;

/**
 * This represents a {@link java.time.LocalDate} format we support deserialization from.
 */
public class LocalDateFormat  implements Format<LocalDate> {
    private String pattern;
    private DateTimeFormatter formatter;
    private Predicate<String> regex;

    public LocalDateFormat(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    @Override
    public LocalDate parse(String dateString) {
        return Try.of(() -> LocalDate.parse(dateString, formatter)).get();
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
