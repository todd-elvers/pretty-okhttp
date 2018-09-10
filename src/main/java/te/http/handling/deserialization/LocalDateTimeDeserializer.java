package te.http.handling.deserialization;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import te.http.handling.deserialization.parsing.DateParser;
import te.http.handling.deserialization.parsing.LocalDateTimeParser;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.util.Collections.singletonList;

public class LocalDateTimeDeserializer implements MultiFormatDateDeserializer<LocalDateTime> {

    @Override
    public Class<LocalDateTime> getTargetClass() {
        return LocalDateTime.class;
    }

    @Override
    public List<DateParser<LocalDateTime>> supportedFormats() {
        return singletonList(
                new LocalDateTimeParser(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]"
                )
        );
    }

    @Override
    public LocalDateTime fromUnixEpoch(long epoch) {
        return Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}