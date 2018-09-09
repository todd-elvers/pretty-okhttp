package te.http.handling.deserialization.domain;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.control.Try;

/**
 * This represents a {@link java.util.Date} format we support deserialization from.
 */
public class DateFormat implements Format<Date> {
    private String pattern;
    private FastDateFormat formatter;
    private Predicate<String> regex;

    public DateFormat(String pattern, String regexForPattern) {
        this.pattern = pattern;
        this.formatter = FastDateFormat.getInstance(pattern);
        this.regex = Pattern.compile(regexForPattern).asPredicate();
    }

    @Override
    public Date parse(String dateString) {
        return Try.of(() -> formatter.parse(dateString)).get();
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

}
