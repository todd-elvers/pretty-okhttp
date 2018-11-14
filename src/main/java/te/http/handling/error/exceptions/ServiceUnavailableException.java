package te.http.handling.error.exceptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;

import okhttp3.Request;
import te.http.handling.error.ExceptionMessageBuilder;
import te.http.handling.error.TimeoutDetector;

//TODO: Rename this to HttpServerException
public class ServiceUnavailableException extends IOException {

    private TimeoutDetector timeoutDetector = new TimeoutDetector();
    private Request request;
    private Throwable rootCause;

    public ServiceUnavailableException(String message, Throwable ex) {
        super(message, ex);
    }

    public ServiceUnavailableException(Throwable throwable, Request request) {
        this(
                new ExceptionMessageBuilder().buildServiceUnavailableMessage(throwable, request),
                throwable
        );

        this.request = request;
        this.rootCause = throwable;
    }

    public Request getRequest() {
        return request;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    /**
     * @return true if, and only if, the exception thrown is a {@link SocketTimeoutException}
     * whose text exactly matches {@link TimeoutDetector#CONNECT_TIMEOUT_TEXT}.
     */
    public boolean wasConnectTimeout() {
        return timeoutDetector.isConnectTimeout(rootCause);
    }

    /**
     * @return true if, and only if, the exception thrown wraps another exception, both of which
     * are {@link SocketTimeoutException}s, and if the wrapped exception's message's text exactly
     * matches {@link TimeoutDetector#READ_TIMEOUT_TEXT}.
     */
    public boolean wasReadTimeout() {
        return timeoutDetector.isReadTimeout(rootCause);
    }
}
