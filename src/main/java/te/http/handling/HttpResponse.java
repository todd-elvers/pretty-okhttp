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
 * read it during instantiation, to fetch the response body yourself use {@link #getBodyAsString}
 * or {@link #getBodyAsBytes}.
 */
public class HttpResponse {

    private @Nullable byte[] body;
    private String statusMessage;
    private int statusCode;
    private Response wrappedResponse;

    // Convenience constructor to simplify testing
    public HttpResponse() {}

    public HttpResponse(Response okHttpResponse) {
        this.wrappedResponse = okHttpResponse;
        this.statusCode = okHttpResponse.code();
        this.statusMessage = okHttpResponse.message();
        this.body = Try.of(okHttpResponse::body).mapTry(ResponseBody::bytes).getOrNull();
    }

    public boolean is200() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isNot200() {
        return !is200();
    }

    public Optional<byte[]> getBodyAsBytes() {
        return Optional.ofNullable(body);
    }

    public Optional<String> getBodyAsString() {
        return getBodyAsBytes().map(String::new);
    }

    public HttpResponse setBody(byte[] body) {
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

    /**
     * @return the wrapped OkHttp {@link Response}.
     */
    public Response getWrappedResponse() {
        return wrappedResponse;
    }

    public HttpResponse setWrappedResponse(Response wrappedResponse) {
        this.wrappedResponse = wrappedResponse;
        return this;
    }

    /**
     * @return the original {@link Request} that was made.
     */
    public Request getRequest() {
        return wrappedResponse.request();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("body", body)
                .append("statusMessage", statusMessage)
                .append("statusCode", statusCode)
                .append("wrappedResponse", wrappedResponse)
                .append("request", getRequest())
                .toString();
    }
}
