package te.http.handling.exceptions;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import te.http.handling.deserialization.domain.Format;


public class LocalDateParsingException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_SUFFIX =
            "To customize the deserialization behavior create your own implementation " +
                    "of MultiFormatLocalDateDeserializer and register it with GSON instead" +
                    " (see JsonMarshalling for how to register a TypeAdapter for GSON).";

    public LocalDateParsingException(String input, Collection<Format<LocalDate>> formatsTried) {
        this(
                input,
                formatsTried.stream().map(Format::getPattern).collect(Collectors.joining(", "))
        );
    }

    private LocalDateParsingException(String input, String formatsTried) {
        super(
                String.format(
                        "LocalDate deserialization failed for [%s].  " +
                                "Tried Unix Epoch and the following formats: [%s]. %s",
                        input,
                        formatsTried,
                        EXCEPTION_MESSAGE_SUFFIX
                )
        );
    }
}
