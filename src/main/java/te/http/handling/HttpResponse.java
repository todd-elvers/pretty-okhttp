package te.http.handling;

import io.vavr.control.Try;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Wrapper around OkHttp's {@link Response} object and the {@link Request} object
 * that was used to retrieve it.
 *
 * Also abstracts away the fact that OkHttp's {@link Response} object only allows
 * you to read a response once by caching it in this object upon instantiation.
 */
public class HttpResponse implements HttpRequestHandling {

    private boolean wasSuccessful;
    private String body;
    private String statusMessage;
    private Integer statusCode;
    private Response originalResponse;
    private Request originalRequest;

    public HttpResponse() {}

    public HttpResponse(Response okHttpResponse) {
        this.wasSuccessful = okHttpResponse.isSuccessful();
        this.originalResponse = okHttpResponse;
        this.originalRequest = okHttpResponse.request();
        this.statusCode = okHttpResponse.code();
        this.statusMessage = okHttpResponse.message();
        this.body = Try.of(okHttpResponse::body).mapTry(ResponseBody::string).getOrNull();
    }

    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    public boolean wasNotSuccessful() {
        return !wasSuccessful;
    }

    public HttpResponse setSuccessful() {
        this.wasSuccessful = true;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public HttpResponse setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public HttpResponse setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Response getOriginalResponse() {
        return originalResponse;
    }

    public HttpResponse setOriginalResponse(Response originalResponse) {
        this.originalResponse = originalResponse;
        return this;
    }

    public Request getOriginalRequest() {
        return originalRequest;
    }

    public HttpResponse setOriginealRequest(Request originalRequest) {
        this.originalRequest = originalRequest;
        return this;
    }

    public String toString() {
        return String.format(
                "%s.%s(statusCode: %d, statusMessage: %s, body: %s)",
                this.getClass().getPackage().getName(),
                this.getClass().getSimpleName(),
                statusCode,
                statusMessage,
                body
        );
    }

}
