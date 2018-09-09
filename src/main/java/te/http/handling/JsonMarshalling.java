package te.http.handling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import te.http.handling.serialization.DateToISO8601Serializer;
import te.http.handling.serialization.LocalDateToISO8601Serializer;
import te.http.handling.deserialization.MultiFormatDateDeserializer;
import te.http.handling.deserialization.MultiFormatLocalDateDeserializer;
import te.http.handling.deserialization.domain.ListParameterizedType;

/**
 * General interface for marshalling objects to/from JSON that comes with built-in defaults to handle the general cases.
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
        Type typeOfListOfT = new ListParameterizedType(classOfT);
        return getJsonMarshaller().fromJson(json, typeOfListOfT);
    }

    interface Defaults {
        Gson jsonMarshaller = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateToISO8601Serializer())
                .registerTypeAdapter(Date.class, new MultiFormatDateDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateToISO8601Serializer())
                .registerTypeAdapter(LocalDate.class, new MultiFormatLocalDateDeserializer())
                .create();
    }
}
