package te.http.handling.error.exceptions;

import java.io.IOException;

import te.http.handling.HttpResponse;
import te.http.handling.error.ExceptionMessageBuilder;

//TODO: Rename this to HttpClientException
public class Non200ResponseException extends IOException {

    private HttpResponse httpResponse;

    public Non200ResponseException(String message) {
        super(message);
    }

    public Non200ResponseException(HttpResponse httpResponse) {
        //TODO: How can we test this?
        this(new ExceptionMessageBuilder().buildNon200ResponseMessage(httpResponse));

        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
