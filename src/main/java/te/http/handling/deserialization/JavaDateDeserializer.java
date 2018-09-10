package te.http.handling.deserialization;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.deserialization.parsing.JavaDateParser;

public class JavaDateDeserializer implements MultiFormatDateDeserializer<Date> {

    @Override
    public Class<Date> getTargetClass() {
        return Date.class;
    }

    @Override
    public List<DateParser<Date>> supportedFormats() {
        return Arrays.asList(
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