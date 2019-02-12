package te.http.handling;

import io.vavr.control.Try;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import te.http.handling.error.exceptions.HttpClientException;
import te.http.handling.error.exceptions.HttpServerException;
import te.http.handling.error.exceptions.NoResponseException;

/**
 * This Java 8 interface encapsulates what is required to make requests to webservices via OkHttp.
 *
 * If you require a special configuration of either the OkHttp client or the JSON marshaller, simply
 * override the relevant methods.
 *
 * @apiNote All fields in this interface are implicitly public, static, and final.
 */
public interface HttpRequestHandling extends GETRequestHandling, POSTRequestHandling, JsonMarshalling {

    MediaType applicationJSON = Defaults.applicationJSON;

    default OkHttpClient getHttpClient() {
        return Defaults.httpClient;
    }

    /**
     * The default assumption made here is that you're trying to send & receive JSON. Override this
     * method if you require different static headers to be included in every request.
     *
     * @apiNote Dynamic headers, e.g. a hash of URL params, should NOT be handled here. Instead,
     * call the relevant buildRequestFor____ method which will return you a {@link Request} object
     * which you can then set the headers on.
     */
    default Headers getDefaultHeaders() {
        return Defaults.jsonHeaders;
    }

    /**
     * @return whether or not a non-200 level response should cause an exception to be thrown
     */
    default boolean isNon200ResponseExceptional() { return true; }

    /**
     * Executes a given {@link Request} and returns the {@link Response} wrapped in a {@link
     * HttpResponse}. Usually one would use the convenience methods in {@link GETRequestHandling}
     * and {@link POSTRequestHandling} instead of calling this directly.
     *
     * @param request the {@link Request} to execute
     * @return a {@link HttpResponse} wrapping the {@link Response} and optionally a response body.
     * @throws NoResponseException if no response is ever received from the webservice; if this was
     *                             due to a connect timeout then {@link NoResponseException#isConnectTimeout()}
     *                             will return true; if this was due to a read timeout then  {@link
     *                             NoResponseException#isReadTimeout()} will return true.
     * @throws HttpServerException if the webservice returned a 500 level status code; this will
     *                             have a reference to the corresponding {@link HttpResponse}
     *                             object.
     * @throws HttpClientException if the webservice returned a 400 level status code; this will
     *                             have a reference to the corresponding {@link HttpResponse}
     *                             object.
     */
    default HttpResponse executeRequest(Request request) throws HttpServerException, HttpClientException, NoResponseException {
        HttpResponse response = Try
                .withResources(() -> getHttpClient().newCall(request).execute())
                .of(HttpResponse::new)
                .getOrElseThrow((exception) -> new NoResponseException(exception, request));

        if (response.isNot200() && isNon200ResponseExceptional()) {
            return handleNon200Response(response);
        }

        return response;
    }

    /**
     * Defines the behavior for when a response has a non-200 level status code (300, 400, 500, etc.).
     *
     * <p>By default, 300 level status codes do nothing, 400 level status codes
     * throw {@link HttpClientException}s and 500 level status codes throw
     * {@link HttpServerException}.
     */
    default HttpResponse handleNon200Response(HttpResponse response) throws HttpClientException, HttpServerException {
        int statusCode = response.getStatusCode();

        if (statusCode >= 400 && statusCode < 500) {
            throw new HttpClientException(response);
        }

        if (statusCode >= 500) {
            throw new HttpServerException(response);
        }

        return response;
    }

    interface Defaults {
        MediaType applicationJSON = MediaType.parse("application/json");

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();

        Headers jsonHeaders = new Headers.Builder()
                .add("Accept", applicationJSON.toString())
                .add("Content-Type", applicationJSON.toString())
                .build();
    }
}
