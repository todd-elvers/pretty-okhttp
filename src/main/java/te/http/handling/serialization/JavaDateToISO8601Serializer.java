package te.http.handling.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;

public class JavaDateToISO8601Serializer implements JsonSerializer<Date> {

    //TODO: Is the Date here ever null?  I don't think it can be...

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(ISO_8601_EXTENDED_DATE_FORMAT.format(src));
//                (src == null) ? "" :
//        );
    }

}
