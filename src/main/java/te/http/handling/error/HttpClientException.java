package te.http.handling.error;

import java.io.IOException;

import te.http.handling.HttpResponse;

/**
 * Represents a response that returned a 400-level status code
 */
public class HttpClientException extends IOException {

    private HttpResponse httpResponse;

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(HttpResponse httpResponse) {
        //TODO: How can we test this?
        this(new ExceptionMessageBuilder().buildNon200ResponseMessage(httpResponse));

        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
