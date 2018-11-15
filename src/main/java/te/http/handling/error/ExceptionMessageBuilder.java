package te.http.handling.error;

import okhttp3.Request;
import te.http.handling.HttpResponse;

public class ExceptionMessageBuilder {

    private TimeoutDetector timeoutDetector = new TimeoutDetector();

    public String buildNoResponseMessage(Throwable throwable, Request request) {
        String errorMessageHeader = "Service is unavailable or unresponsive!";
        String requestedURL = request.url().toString();

        if (timeoutDetector.isConnectTimeout(throwable)) {
            errorMessageHeader = "Connect timeout occurred!";
        } else if (timeoutDetector.isReadTimeout(throwable)) {
            errorMessageHeader = "Read timeout occurred!";
        }

        return String.format(
                "%s" +
                        "\n\tURL = %s" +
                        "\n\tMethod = %s" +
                        "\n",
                errorMessageHeader,
                requestedURL,
                request.method()
        );
    }

    public String buildNon200ResponseMessage(HttpResponse httpResponse) {
        return String.format(
                "Request returned %d!" +
                        "\n\tURL = %s" +
                        "\n\tMethod = %s" +
                        "\n\tMessage = %s" +
                        "\n\tBody = %s" +
                        "\n",
                httpResponse.getStatusCode(),
                httpResponse.getRequest().url().toString(),
                httpResponse.getRequest().method(),
                httpResponse.getStatusMessage(),
                httpResponse.getBody().orElse(null)
        );
    }

}