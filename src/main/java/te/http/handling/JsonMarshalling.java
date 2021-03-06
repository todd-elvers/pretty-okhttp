package te.http.handling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import te.http.handling.deserialization.JavaDateDeserializer;
import te.http.handling.deserialization.ListParameterizedType;
import te.http.handling.deserialization.LocalDateDeserializer;
import te.http.handling.deserialization.LocalDateTimeDeserializer;
import te.http.handling.serialization.JavaDateToISO8601Serializer;
import te.http.handling.serialization.LocalDateTimeToISO8601Serializer;
import te.http.handling.serialization.LocalDateToISO8601Serializer;

/**
 * Simple JSON marshalling interface for serialization/deserialization via Google's
 * {@link Gson} library.  Also comes with sensible date handling defaults that
 * can be easily overridden.
 *
 * <p>The default behavior is to add {@link Date}, {@link LocalDate}, and {@link LocalDateTime}
 * serialization & deserialization handling to {@link Gson} during initialization.
 */
public interface JsonMarshalling {

    default Gson getJsonMarshaller() {
        return Defaults.jsonMarshaller;
    }

    default String toJson(Object object) {
        return getJsonMarshaller().toJson(object);
    }

    default <T> T fromJson(String json, Class<T> classOfT) {
        return getJsonMarshaller().fromJson(json, classOfT);
    }

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
