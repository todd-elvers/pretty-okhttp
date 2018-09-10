package te.http.handling.exceptions;

public class ParserConfigurationException extends RuntimeException {

    public ParserConfigurationException(Throwable ex, String pattern, String input) {
        super(
                String.format(
                        "%s was matched by the pattern '%s', " +
                                "but then could not be parsed by the associated parser for that pattern. " +
                                "Ensure your parser is configured correctly.",
                        input,
                        pattern
                ),
                ex
        );
    }

}
