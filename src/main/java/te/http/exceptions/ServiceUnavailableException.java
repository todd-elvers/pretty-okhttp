package te.http.exceptions;

import java.io.IOException;

import okhttp3.Request;

public class ServiceUnavailableException extends IOException {

    public Request request;
    public Throwable rootCause;

    public ServiceUnavailableException(Throwable exception, Request request) {
        super(
                String.format("%s is down or not responding.", request.url().toString()),
                exception
        );

        this.request = request;
        this.rootCause = exception;
    }

}
