package te.http.handling.json.deserialize.date;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import io.vavr.collection.List;
import te.http.handling.json.deserialize.date.parsing.DateParser;
import te.http.handling.json.deserialize.date.parsing.LocalDateTimeParser;

public class LocalDateTimeDeserializer implements MultiFormatDateDeserializer<LocalDateTime> {

    @Override
    public List<DateParser<LocalDateTime>> supportedFormats() {
        return List.of(
                new LocalDateTimeParser(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]"
                )
        );
    }

    @Override
    public LocalDateTime fromUnixEpoch(long epoch) {
        return Instant
                .ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}