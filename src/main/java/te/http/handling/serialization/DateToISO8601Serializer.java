package te.http.handling.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.reflect.Type;
import java.util.Date;

public class DateToISO8601Serializer implements JsonSerializer<Date> {
    private static final FastDateFormat ISO8601_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        String formattedDate = (src == null) ? "" : ISO8601_FORMATTER.format(src);

        return new JsonPrimitive(formattedDate);
    }

}
