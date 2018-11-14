package te.http.handling;

import io.vavr.control.Try;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import te.http.handling.error.exceptions.Non200ResponseException;
import te.http.handling.error.exceptions.ServiceUnavailableException;

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
    MediaType applicationXML = Defaults.applicationXML;

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
     * Executes a given {@link Request} and returns the {@link Response} wrapped in a {@link
     * HttpResponse}. Usually one would use the convenience methods in {@link GETRequestHandling}
     * and {@link POSTRequestHandling} instead of calling this directly.
     *
     * @param request the {@link Request} to execute
     * @return a {@link HttpResponse} containing the {@link Response} and the {@link Request} that
     * generated it.
     * @throws ServiceUnavailableException if and only if the webservice never responds; this
     *                                     exception contains the {@link Request} that caused it.
     * @throws Non200ResponseException     if and only if the webservice responses with a non-200;
     *                                     when thrown this exception will contain an {@link
     *                                     HttpResponse}, which will contain a reference to the
     *                                     associated {@link Request} & {@link Response} to simplify
     *                                     accessing the response body, checking headers, etc.
     */
    default HttpResponse executeRequest(Request request) throws ServiceUnavailableException, Non200ResponseException {
        HttpResponse response = Try
                .withResources(() -> getHttpClient().newCall(request).execute())
                .of(HttpResponse::new)
                .getOrElseThrow((exception) -> new ServiceUnavailableException(exception, request));

        if (response.wasNotSuccessful()) {
            throw new Non200ResponseException(response);
        }

        return response;
    }


    interface Defaults {
        MediaType applicationJSON = MediaType.parse("application/json");
        MediaType applicationXML = MediaType.parse("application/xml");

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();

        Headers jsonHeaders = new Headers.Builder()
                .add("Accept", applicationJSON.toString())
                .add("Content-Type", applicationJSON.toString())
                .build();
    }
}
