package te.http.handling;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

import javax.annotation.Nullable;

import io.vavr.control.Try;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Wrapper around OkHttp's {@link Response} object that eagerly fetches the response
 * body and closes the buffer, ensuring no memory leaks occur.
 *
 * Since OkHttp's {@link Response} only allows one read of the response body, and since we
 * read it during instantiation, to fetch the response body yourself use {@link #body} and
 * <b>not</b> {@link Response#body()}.
 */
public class HttpResponse {

    private boolean isSuccessful;
    private @Nullable String body;
    private String statusMessage;
    private int statusCode;
    private Response wrappedResponse;
    private Request request;

    // Convenience constructor to simplify testing
    public HttpResponse() {}

    public HttpResponse(Response okHttpResponse) {
        this.isSuccessful = okHttpResponse.isSuccessful();
        this.wrappedResponse = okHttpResponse;
        this.request = okHttpResponse.request();
        this.statusCode = okHttpResponse.code();
        this.statusMessage = okHttpResponse.message();
        this.body = Try.of(okHttpResponse::body).mapTry(ResponseBody::string).getOrNull();
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public boolean isNotSuccessful() {
        return !isSuccessful;
    }

    public HttpResponse setSuccessful() {
        this.isSuccessful = true;
        return this;
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public String getBodyOrNull() {
        return Optional.ofNullable(body).orElse(null);
    }

    public String getBodyOrDefault(String defaultString) {
        return Optional.ofNullable(body).orElse(defaultString);
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

    public int getStatusCode() {
        return statusCode;
    }

    public HttpResponse setStatusCode(int statusCode) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isSuccessful", isSuccessful)
                .append("body", body)
                .append("statusMessage", statusMessage)
                .append("statusCode", statusCode)
                .append("wrappedResponse", wrappedResponse)
                .append("request", request)
                .toString();
    }
}
