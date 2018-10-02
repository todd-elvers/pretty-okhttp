package te.http.handling.exceptions;


import java.time.DateTimeException;

public class DateTimeDeserializationException extends DateTimeException {

    public DateTimeDeserializationException(String input, String formatsTried, Class<?> targetClass) {
        super(
                String.format(
                        "%s deserialization failed for [%s].  " +
                                "Tried Unix Epoch and the following formats: [%s].",
                        targetClass.getSimpleName(),
                        input,
                        formatsTried
                )
        );
    }
}
