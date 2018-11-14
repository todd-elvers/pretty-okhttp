package te.http.handling;

import io.vavr.control.Try;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Wrapper around OkHttp's {@link Response} object that eagerly fetches the response
 * body and closes the buffer, ensuring no memory leaks occur.
 *
 * The eager fetching of the response body also abstracts away the fact that OkHttp's
 * {@link Response} object only allows you to read a response once.  No more checking
 * if the buffer has already been read!
 */
public class HttpResponse {

    private boolean wasSuccessful;
    private String body;
    private String statusMessage;
    private Integer statusCode;
    private Response wrappedResponse;
    private Request request;

    // Convenience constructor to simplify testing
    public HttpResponse() {}

    public HttpResponse(Response okHttpResponse) {
        this.wasSuccessful = okHttpResponse.isSuccessful();
        this.wrappedResponse = okHttpResponse;
        this.request = okHttpResponse.request();
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

    public Response getWrappedResponse() {
        return wrappedResponse;
    }

    public HttpResponse setWrappedResponse(Response wrappedResponse) {
        this.wrappedResponse = wrappedResponse;
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public HttpResponse setRequest(Request request) {
        this.request = request;
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
