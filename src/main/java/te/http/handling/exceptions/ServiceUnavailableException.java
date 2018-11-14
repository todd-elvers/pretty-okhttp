package te.http.handling.exceptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;

import okhttp3.Request;

public class ServiceUnavailableException extends IOException {
    private static final String READ_TIMEOUT_TEXT = "Read timed out";
    private static final String CONNECT_TIMEOUT_TEXT = "connect timed out";

    private Request request;
    private Throwable rootCause;

    public ServiceUnavailableException(String message, Throwable ex) {
        super(message, ex);
    }

    public ServiceUnavailableException(Throwable exception, Request request) {
        this(
                String.format("%s is down or not responding.", request.url().toString()),
                exception
        );

        this.request = request;
        this.rootCause = exception;
    }

    public Request getRequest() {
        return request;
    }

    public Throwable getRootCause() {
        return rootCause;
    }


    /**
     * @return true if, and only if, the exception thrown is a {@link SocketTimeoutException}
     * whose text exactly matches {@link #CONNECT_TIMEOUT_TEXT}.
     */
    public boolean wasConnectTimeout() {
        if (rootCause instanceof SocketTimeoutException) {
            return Optional.of(getRootCause())
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
    public boolean wasReadTimeout() {
        if (rootCause instanceof SocketTimeoutException) {
            return Optional.of(getRootCause().getCause())
                    .filter(cause -> cause instanceof SocketTimeoutException)
                    .map(Throwable::fillInStackTrace)
                    .map(Throwable::getMessage)
                    .map(message -> message.contains(READ_TIMEOUT_TEXT))
                    .orElse(false);
        }

        return false;
    }
}
