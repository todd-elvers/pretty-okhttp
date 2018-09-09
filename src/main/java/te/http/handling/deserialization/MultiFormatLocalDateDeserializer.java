package te.http.handling.deserialization;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import te.http.handling.deserialization.domain.Format;
import te.http.handling.deserialization.domain.LocalDateFormat;
import te.http.handling.exceptions.LocalDateParsingException;

public class MultiFormatLocalDateDeserializer implements MultiFormatDeserializer<LocalDate> {

    @Override
    public List<Format<LocalDate>> supportedFormats() {
        return Arrays.asList(
                new LocalDateFormat("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                new LocalDateFormat("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
                new LocalDateFormat("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
        );
    }

    @Override
    public LocalDate fromUnixEpoch(long epoch) {
        return Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Override
    public RuntimeException onFailure(String dateString, List<Format<LocalDate>> formatsAttempted) {
        return new LocalDateParsingException(dateString, supportedFormats());
    }

}