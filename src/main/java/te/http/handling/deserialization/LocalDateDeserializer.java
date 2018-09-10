package te.http.handling.deserialization;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.deserialization.parsing.LocalDateParser;

public class LocalDateDeserializer implements MultiFormatDateDeserializer<LocalDate> {

    @Override
    public List<DateParser<LocalDate>> supportedFormats() {
        return Arrays.asList(
                new LocalDateParser("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                new LocalDateParser("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
                new LocalDateParser("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
        );
    }

    @Override
    public LocalDate fromUnixEpoch(long epoch) {
        return Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Override
    public Class<LocalDate> getTargetClass() {
        return LocalDate.class;
    }


}