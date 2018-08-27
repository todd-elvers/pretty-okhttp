package te.http.exceptions;

import java.io.IOException;

import te.http.handling.HttpResponse;

public class Non200ResponseException extends IOException {

    public HttpResponse httpResponse;

    public Non200ResponseException(HttpResponse httpResponse) {
        super(
                String.format(
                        "Request returned non-200!" +
                                "\n\tURL = %s" +
                                "\n\tCode = %d" +
                                "\n\tMessage = %s" +
                                "\n\tBody = %s" +
                                "\n",
                        httpResponse.getOriginalRequest().url().toString(),
                        httpResponse.getStatusCode(),
                        httpResponse.getStatusMessage(),
                        httpResponse.getBody()
                )
        );

        this.httpResponse = httpResponse;
    }

}
