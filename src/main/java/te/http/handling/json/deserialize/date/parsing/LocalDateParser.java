package te.http.handling.json.deserialize.date.parsing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This represents a {@link java.time.LocalDate} format we support deserialization from.
 */
public class LocalDateParser implements DateParser<LocalDate> {
    private String pattern;
    private Predicate<String> regex;
    private DateTimeFormatter formatter;

    public LocalDateParser(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    @Override
    public LocalDate parseDateString(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, formatter);
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

}
