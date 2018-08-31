package te.http.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import te.http.serialization.domain.LocalDateFormat;


public class LocalDateParsingException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_SUFFIX = "To customize the deserialization behavior create your own implementation " +
            "of MultiFormatLocalDateDeserializer and register it instead (see JsonMarshalling " +
            "for how to register a TypeAdapter for GSON).";

    public LocalDateParsingException(String input, Collection<LocalDateFormat> formatsTried) {
        super(
                String.format(
                        "LocalDate deserialization failed for [%s].  Tried Unix Epoch and the following formats: [%s]. %s",
                        input,
                        formatsTried.stream().map(LocalDateFormat::getPattern).collect(Collectors.joining(", ")),
                        EXCEPTION_MESSAGE_SUFFIX
                )
        );
    }
}
