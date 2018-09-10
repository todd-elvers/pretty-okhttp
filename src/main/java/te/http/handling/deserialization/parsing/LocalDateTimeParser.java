package te.http.handling.deserialization.parsing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This represents a {@link LocalDate} format we support deserialization from.
 */
public class LocalDateTimeParser implements DateParser<LocalDateTime> {
    private String pattern;
    private Predicate<String> regex;
    private DateTimeFormatter formatter;

    public LocalDateTimeParser(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    @Override
    public LocalDateTime parseDateString(String dateString) throws DateTimeParseException {
        return LocalDateTime.parse(dateString, formatter);
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

}
