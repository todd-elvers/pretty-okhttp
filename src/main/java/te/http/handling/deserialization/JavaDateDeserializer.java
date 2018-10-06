package te.http.handling.deserialization;

import java.util.Date;

import io.vavr.collection.List;
import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.deserialization.parsing.JavaDateParser;

public class JavaDateDeserializer implements MultiFormatDateDeserializer<Date> {

    @Override
    public List<DateParser<Date>> supportedFormats() {
        return List.of(
                new JavaDateParser("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                new JavaDateParser("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
                new JavaDateParser("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
        );
    }

    @Override
    public Date fromUnixEpoch(long epoch) {
        return new Date(epoch);
    }

}