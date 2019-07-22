package te.http.handling.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import te.http.handling.json.deserialize.ListParameterizedType;
import te.http.handling.json.deserialize.date.JavaDateDeserializer;
import te.http.handling.json.deserialize.date.LocalDateDeserializer;
import te.http.handling.json.deserialize.date.LocalDateTimeDeserializer;
import te.http.handling.json.serialize.date.JavaDateToISO8601Serializer;
import te.http.handling.json.serialize.date.LocalDateTimeToISO8601Serializer;
import te.http.handling.json.serialize.date.LocalDateToISO8601Serializer;

/**
 * Simple JSON serialization/deserialization wrapper that delegates calls to an instance of Google's
 * {@link Gson} library that has been preconfigured with some sensible defaults. <br/><br/>
 *
 * <h2>Added functionality:</h3>
 * <ol>
 * <li>Serialization/deserialization support added for {@link Date}, {@link LocalDate}, and {@link
 * LocalDateTime} in multiple common formats (Unix time, ISO-8601, etc.)</li>
 * <li><code>fromJsonList(String, Class&lt;T&gt;)</code> for de-serializing JSON strings that that
 * are an array of objects with type safety
 * </ol><br/><br/>
 *
 * <h2>Gson customization</h2>
 * To customize the {@link Gson} instance that is used override {@link #getJsonMarshaller()} and
 * provide your own configured instance.  If you'd like to tweak the existing marshaller in one
 * way or another override the same method and in it use <code>Defaults.jsonMarshaller.newBuilder()</code>
 * to get a {@link GsonBuilder} copy you can use to build a {@link Gson} instance from the defaults.
 */
public interface JsonMarshalling {

    /**
     * @return a reference to the static {@link Gson} instance used for all JSON marshalling
     * (override this method to customize the {@link Gson} instance).
     */
    default Gson getJsonMarshaller() {
        return Defaults.jsonMarshaller;
    }

    default String toJson(Object anything) {
        return getJsonMarshaller().toJson(anything);
    }

    /**
     * Converts a blob of JSON into an object of type T.
     */
    default <T> T fromJson(String json, Class<T> classOfT) {
        return getJsonMarshaller().fromJson(json, classOfT);
    }

    /**
     * Converts a blob of JSON into a object of type List&lt;T&gt;.  Use this method when your
     * JSON blob represents an array of objects instead of a single object itself.
     */
    default <T> List<T> fromJsonList(String json, Class<T> classOfT) {
        return getJsonMarshaller().fromJson(json, new ListParameterizedType(classOfT));
    }

    interface Defaults {
        Gson jsonMarshaller = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JavaDateDeserializer())
                .registerTypeAdapter(Date.class, new JavaDateToISO8601Serializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateToISO8601Serializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeToISO8601Serializer())
                .create();
    }
}
