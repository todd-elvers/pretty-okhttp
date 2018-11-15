package te.http.handling.error;

import java.net.SocketTimeoutException;
import java.util.Optional;

/**
 * Tool for distinguishing between socket connect timeouts and socket read timeouts.
 */
public class TimeoutDetector {
    private static final String READ_TIMEOUT_TEXT = "Read timed out";
    private static final String CONNECT_TIMEOUT_TEXT = "connect timed out";

    /**
     * @return true if, and only if, the exception thrown is a {@link SocketTimeoutException}
     * whose text exactly matches {@link #CONNECT_TIMEOUT_TEXT}.
     */
    public boolean isConnectTimeout(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            return Optional.of(throwable)
                    .map(Throwable::fillInStackTrace)
                    .map(Throwable::getMessage)
                    .map(msg -> msg.contains(CONNECT_TIMEOUT_TEXT))
                    .orElse(false);
        }

        return false;
    }

    /**
     * @return true if, and only if, the exception thrown wraps another exception, both of which
     * are {@link SocketTimeoutException}s, and if the wrapped exception's message's text exactly
     * matches {@link #READ_TIMEOUT_TEXT}.
     */
    public boolean isReadTimeout(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException && throwable.getCause() != null) {
            return Optional.of(throwable.getCause())
                    .filter(cause -> cause instanceof SocketTimeoutException)
                    .map(Throwable::fillInStackTrace)
                    .map(Throwable::getMessage)
                    .map(message -> message.contains(READ_TIMEOUT_TEXT))
                    .orElse(false);
        }

        return false;
    }

}
