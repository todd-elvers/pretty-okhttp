package te.http.handling.deserialization.parsing;

import org.apache.commons.lang3.time.FastDateFormat;

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
    public Date parseDateString(String dateString) throws Exception {
        return formatter.parse(dateString);
    }

    public String getPattern() {
        return pattern;
    }

    public Predicate<String> getRegex() {
        return regex;
    }

}
