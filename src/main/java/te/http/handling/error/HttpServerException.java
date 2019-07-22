package te.http.handling.error;

import java.io.IOException;

import te.http.handling.HttpResponse;

/**
 * Represents a request that failed due to something server-side,
 * e.g. a server with a failing dependency, etc.
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
