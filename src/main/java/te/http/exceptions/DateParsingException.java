package te.http.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import te.http.serialization.domain.DateFormat;


public class DateParsingException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_SUFFIX = "To customize the deserialization behavior create your own implementation " +
            "of MultiFormatDateDeserializer and register it instead (see JsonMarshalling for more details).";

    public DateParsingException(String input, Collection<DateFormat> formatsTried) {
        super(
                String.format(
                        "Date deserialization failed for [%s].  Tried Unix Epoch and the following formats: [%s]. %s",
                        input,
                        formatsTried.stream().map(DateFormat::getPattern).collect(Collectors.joining(", ")),
                        EXCEPTION_MESSAGE_SUFFIX
                )
        );
    }

}
