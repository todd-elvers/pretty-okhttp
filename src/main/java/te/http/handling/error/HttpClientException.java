package te.http.handling.error;

import java.io.IOException;

import te.http.handling.HttpResponse;

/**
 * Represents a request that failed due to something on the client-side,
 * e.g. invalid authentication credentials, etc.
 */
public class HttpClientException extends IOException {

    private HttpResponse httpResponse;

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(HttpResponse httpResponse) {
        this(new ExceptionMessageBuilder().buildNon200ResponseMessage(httpResponse));

        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
