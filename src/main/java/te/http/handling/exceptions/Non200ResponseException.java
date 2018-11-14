package te.http.handling.exceptions;

import java.io.IOException;

import te.http.handling.HttpResponse;

public class Non200ResponseException extends IOException {

    private HttpResponse httpResponse;

    public Non200ResponseException(String message) {
        super(message);
    }

    public Non200ResponseException(HttpResponse httpResponse) {
        this(
                String.format(
                        "Request returned non-200!" +
                                "\n\tURL = %s" +
                                "\n\tCode = %d" +
                                "\n\tMessage = %s" +
                                "\n\tBody = %s" +
                                "\n",
                        httpResponse.getRequest().url().toString(),
                        httpResponse.getStatusCode(),
                        httpResponse.getStatusMessage(),
                        httpResponse.getBody()
                )
        );

        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
