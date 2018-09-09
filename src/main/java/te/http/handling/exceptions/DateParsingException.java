package te.http.handling.exceptions;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import te.http.handling.deserialization.domain.Format;


public class DateParsingException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_SUFFIX =
            "To customize the deserialization behavior create your own implementation " +
                    "of MultiFormatDateDeserializer and register it with GSON instead" +
                    " (see JsonMarshalling for how to register a TypeAdapter for GSON).";

    public DateParsingException(String input, Collection<Format<Date>> formatsTried) {
        this(
                input,
                formatsTried.stream().map(Format::getPattern).collect(Collectors.joining(", "))
        );
    }

    private DateParsingException(String input, String formatsTried) {
        super(
                String.format(
                        "Date deserialization failed for [%s].  " +
                                "Tried Unix Epoch and the following formats: [%s]. %s",
                        input,
                        formatsTried,
                        EXCEPTION_MESSAGE_SUFFIX
                )
        );
    }

}
