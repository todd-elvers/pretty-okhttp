package te.http.handling.exceptions;

import java.io.IOException;

import okhttp3.Request;

public class ServiceUnavailableException extends IOException {

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
}
