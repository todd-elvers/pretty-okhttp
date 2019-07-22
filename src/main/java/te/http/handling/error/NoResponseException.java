package te.http.handling.error;

import java.io.IOException;

import okhttp3.Request;

/**
 * Represents a request that failed due to the server never responding.
 */
public class NoResponseException extends IOException {

    private TimeoutDetector timeoutDetector = new TimeoutDetector();
    private Request request;
    private Throwable rootCause;

    public NoResponseException(String message, Throwable ex) {
        super(message, ex);
    }

    public NoResponseException(Throwable throwable, Request request) {
        this(
                new ExceptionMessageBuilder().buildNoResponseMessage(throwable, request),
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
     * @return true if, and only if, there was no response from the requested resource
     * due to a timeout while establishing a connection.
     */
    public boolean isConnectTimeout() {
        return timeoutDetector.isConnectTimeout(rootCause);
    }

    /**
     * @return true if, and only if, there was no response from the requested resource
     * due to a timeout while reading the response.
     */
    public boolean isReadTimeout() {
        return timeoutDetector.isReadTimeout(rootCause);
    }
}
