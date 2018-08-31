package te.http.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.control.Try;
import te.http.exceptions.LocalDateParsingException;
import te.http.serialization.domain.DateFormat;
import te.http.serialization.domain.LocalDateFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class MultiFormatLocalDateDeserializer implements JsonDeserializer<LocalDate> {

    protected static final Predicate<String> IS_UNIX_EPOCH = Pattern.compile("[0-9]{9,}").asPredicate();

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return parseDate(json.getAsJsonPrimitive().getAsString());
        } catch (RuntimeException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /**
     * @return the {@link LocalDateFormat}s this class will attempt during deserialization
     */
    protected List<LocalDateFormat> supportedDateFormats() {
        return Arrays.asList(
                new LocalDateFormat("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                new LocalDateFormat("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
                new LocalDateFormat("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
        );
    }

    protected LocalDate parseDate(String dateString) throws RuntimeException {
        if(isBlank(dateString)) return null;
        if(IS_UNIX_EPOCH.test(dateString)) {
            return Instant.ofEpochMilli(Long.parseLong(dateString)).atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return findLocalDateParserFor(dateString)
                .map(dateParser -> Try.of(() -> LocalDate.parse(dateString, dateParser)).getOrNull())
                .orElseThrow(() -> new LocalDateParsingException(dateString, supportedDateFormats()));
    }

    private Optional<DateTimeFormatter> findLocalDateParserFor(String dateString) {
        return supportedDateFormats().stream()
                .filter(dateFormat -> dateFormat.matches(dateString))
                .map(LocalDateFormat::getFormatter)
                .findFirst();
    }

}