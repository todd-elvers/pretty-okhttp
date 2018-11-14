package te.http.handling.error.exceptions;


import java.lang.reflect.Type;
import java.time.DateTimeException;

import io.vavr.collection.List;
import te.http.handling.deserialization.parsing.DateParser;

import static java.util.stream.Collectors.joining;

public class DateTimeDeserializationException extends DateTimeException {

    public <T> DateTimeDeserializationException(String input, Type targetClass, List<DateParser<T>> formatsTried) {
        super(
                String.format(
                        "%s deserialization failed for '%s'.  " +
                                "Tried Unix Epoch and the following formats: [%s].",
                        targetClass.getTypeName(),
                        input,
                        formatsTried.map(DateParser::getPattern).collect(joining(", "))
                )
        );
    }

}
