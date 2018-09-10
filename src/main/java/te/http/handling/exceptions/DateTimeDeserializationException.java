package te.http.handling.exceptions;


import java.time.DateTimeException;

public class DateTimeDeserializationException extends DateTimeException {

    public DateTimeDeserializationException(String input, String formatsTried, Class<?> parserClass) {
        super(
                String.format(
                        "%s deserialization failed for [%s].  " +
                                "Tried Unix Epoch and the following formats: [%s].",
                        parserClass.getSimpleName(),
                        input,
                        formatsTried
                )
        );
    }
}
