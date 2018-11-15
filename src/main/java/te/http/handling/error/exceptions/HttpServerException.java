package te.http.handling.error.exceptions;

import java.io.IOException;

import te.http.handling.HttpResponse;
import te.http.handling.error.ExceptionMessageBuilder;

/**
 * Represents a response that returned a 500-level status code
 */
public class HttpServerException extends IOException {

    private HttpResponse httpResponse;

    public HttpServerException(String message) {
        super(message);
    }

    public HttpServerException(HttpResponse httpResponse) {
        this(new ExceptionMessageBuilder().buildNon200ResponseMessage(httpResponse));

        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
