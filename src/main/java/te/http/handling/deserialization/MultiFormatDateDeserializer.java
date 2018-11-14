package te.http.handling.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.collection.List;
import io.vavr.control.Try;
import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.error.exceptions.DateTimeDeserializationException;

import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A {@link JsonDeserializer} that attempts all {@link DateParser}s returned from {@link
 * #supportedFormats()} during deserialization.
 */
public interface MultiFormatDateDeserializer<T> extends JsonDeserializer<T> {
    Predicate<String> isUnixEpoch = Pattern.compile("[0-9]{9,}").asPredicate();

    /**
     * @return the formats this class supports for deserialization
     */
    List<DateParser<T>> supportedFormats();

    /**
     * @return a new instance of type T built from the Unix Epoch
     */
    T fromUnixEpoch(long epoch);

    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws DateTimeDeserializationException {
        return Try.of(json::getAsString)
                .mapTry(dateString -> parseDateString(dateString, typeOfT))
                .get();
    }

    default T parseDateString(String dateString, Type typeOfT) {
        if (isBlank(dateString)) return null;
        if (isUnixEpoch.test(dateString)) return fromUnixEpoch(parseLong(dateString));

        return supportedFormats()
                .find(dateFormat -> dateFormat.matches(dateString))
                .map(dateFormat -> dateFormat.parse(dateString))
                .getOrElseThrow(() ->
                        new DateTimeDeserializationException(dateString, typeOfT, supportedFormats())
                );
    }
}
