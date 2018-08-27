package te.http.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.control.Try;
import te.http.exceptions.DateParsingException;
import te.http.serialization.domain.DateFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class MultiFormatDateDeserializer implements JsonDeserializer<Date> {

    private static final Predicate<String> IS_UNIX_EPOCH = Pattern.compile("[0-9]{9,}").asPredicate();
    private static final List<DateFormat> DATE_FORMATS = Arrays.asList(
            new DateFormat("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
            new DateFormat("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
            new DateFormat("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
    );

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return parseDate(json.getAsJsonPrimitive().getAsString());
        } catch (RuntimeException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    protected Date parseDate(String dateString) throws RuntimeException {
        if(isBlank(dateString)) return null;
        if(IS_UNIX_EPOCH.test(dateString)) {
            return new Date(Long.parseLong(dateString));
        }

        return findDateParserFor(dateString)
                .map(dateParser -> Try.of(() -> dateParser.parse(dateString)).getOrNull())
                .orElseThrow(() -> new DateParsingException(dateString, DATE_FORMATS));
    }

    private Optional<FastDateFormat> findDateParserFor(String dateString) {
        return DATE_FORMATS.stream()
                .filter(dateFormat -> dateFormat.getRegex().test(dateString))
                .map(DateFormat::getFormatter)
                .findFirst();
    }

}