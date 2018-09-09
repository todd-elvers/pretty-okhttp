package te.http.handling.deserialization;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import te.http.handling.deserialization.domain.DateFormat;
import te.http.handling.deserialization.domain.Format;
import te.http.handling.exceptions.DateParsingException;

public class MultiFormatDateDeserializer implements MultiFormatDeserializer<Date> {

    @Override
    public List<Format<Date>> supportedFormats() {
        return Arrays.asList(
                new DateFormat("yyyy-MM-dd", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                new DateFormat("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}"),
                new DateFormat("MM-dd-yyyy", "[0-9]{2}-[0-9]{2}-[0-9]{4}")
        );
    }

    @Override
    public Date fromUnixEpoch(long epoch) {
        return new Date(epoch);
    }

    @Override
    public RuntimeException onFailure(String dateString, List<Format<Date>> formatsAttempted) {
        return new DateParsingException(dateString, formatsAttempted);
    }

}