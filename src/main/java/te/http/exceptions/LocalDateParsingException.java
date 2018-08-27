package te.http.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import te.http.serialization.domain.LocalDateFormat;


public class LocalDateParsingException extends RuntimeException {

    public LocalDateParsingException(String input, Collection<LocalDateFormat> formatsTried) {
        super(
                String.format(
                        "LocalDate deserialization failed for [%s].  Tried Unix Epoch and the following formats: [%s].  If you have a new date format you would like parsed, add it to MultiFormatLocalDateDeserializer.java.",
                        input,
                        formatsTried.stream().map(LocalDateFormat::getPattern).collect(Collectors.joining(", "))
                )
        );
    }
}
