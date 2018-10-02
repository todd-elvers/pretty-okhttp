package te.http.handling.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

import io.vavr.collection.List;
import io.vavr.control.Try;
import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.exceptions.DateTimeDeserializationException;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A {@link JsonDeserializer} that attempts all {@link DateParser}s returned
 * from {@link #supportedFormats()} during deserialization.
 */
public interface MultiFormatDateDeserializer<T> extends JsonDeserializer<T> {

    /**
     * @return the object this class generates during deserialization
     */
    Class<T> getTargetClass();

    /**
     * @return the formats this class supports for deserialization
     */
    List<DateParser<T>> supportedFormats();

    /**
     * @return a new instance of type T built from the Unix Epoch
     */
    T fromUnixEpoch(long epoch);

    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Try.of(() -> json)
                .mapTry(JsonElement::getAsJsonPrimitive)
                .mapTry(JsonPrimitive::getAsString)
                .mapTry(this::parseDateString)
                .getOrElseThrow(JsonParseException::new);
    }

    default T parseDateString(String dateString) {
        if (isBlank(dateString)) return null;
        if (isUnixEpoch(dateString)) return fromUnixEpoch(parseLong(dateString));

        return supportedFormats()
                .find(dateFormat -> dateFormat.matches(dateString))
                .map(dateFormat ->  dateFormat.parse(dateString))
                .getOrElseThrow(() -> onFailure(dateString, supportedFormats()));
    }

    default DateTimeDeserializationException onFailure(String dateString, List<DateParser<T>> formatsAttempted) {
        return new DateTimeDeserializationException(
                dateString,
                formatsAttempted.map(DateParser::getPattern).collect(joining(", ")),
                getTargetClass()
        );
    }

    default boolean isUnixEpoch(String dateString) {
        return Pattern.compile("[0-9]{9,}").asPredicate().test(dateString);
    }
}
