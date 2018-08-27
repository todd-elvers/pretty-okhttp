package te.http.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import te.http.serialization.domain.DateFormat;


public class DateParsingException extends RuntimeException {

    public DateParsingException(String input, Collection<DateFormat> formatsTried) {
        super(
                String.format(
                        "Date deserialization failed for [%s].  Tried Unix Epoch and the following formats: [%s].  If you have a new date format you would like parsed, add it to MultiFormatDateDeserializer.java.",
                        input,
                        formatsTried.stream().map(DateFormat::getPattern).collect(Collectors.joining(", "))
                )
        );
    }

}
