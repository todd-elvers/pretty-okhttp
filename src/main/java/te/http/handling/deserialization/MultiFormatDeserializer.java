package te.http.handling.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vavr.control.Try;
import te.http.handling.deserialization.domain.Format;

import static org.apache.commons.lang3.StringUtils.isBlank;

public interface MultiFormatDeserializer<T> extends JsonDeserializer<T> {
    Predicate<String> IS_UNIX_EPOCH = Pattern.compile("[0-9]{9,}").asPredicate();

    /**
     * @return The formats this deserializer attempts.
     */
    List<Format<T>> supportedFormats();

    T fromUnixEpoch(long epoch);

    RuntimeException onFailure(String dateString, List<Format<T>> formatsAttempted);

    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Try.of(() -> json)
                .mapTry(JsonElement::getAsJsonPrimitive)
                .mapTry(JsonPrimitive::getAsString)
                .mapTry(this::parseDateString)
                .getOrElseThrow((ex) -> new JsonParseException(ex.getMessage(), ex));
    }

    default T parseDateString(String dateString) throws RuntimeException {
        if (isBlank(dateString)) {
            return null;
        }

        if (IS_UNIX_EPOCH.test(dateString)) {
            return fromUnixEpoch(Long.parseLong(dateString));
        }

        return supportedFormats().stream()
                .filter(dateFormat -> dateFormat.matches(dateString))
                .findFirst()
                .map(dateFormat -> dateFormat.parse(dateString))
                .orElseThrow(() -> onFailure(dateString, supportedFormats()));
    }

}
