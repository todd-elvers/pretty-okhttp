package te.http.handling.json.deserialize.date.parsing;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This represents a {@link java.util.Date} format we support deserialization from.
 */
public class JavaDateParser implements DateParser<Date> {
    private String pattern;
    private Predicate<String> regex;
    private FastDateFormat formatter;

    public JavaDateParser(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = FastDateFormat.getInstance(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    @Override
    public Date parseDateString(String dateString) throws DateTimeParseException {
        try {
            return formatter.parse(dateString);
        } catch (ParseException exception) {
            throw new DateTimeParseException(
                    exception.getMessage(),
                    dateString,
                    exception.getErrorOffset()
            );
        }
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

}
