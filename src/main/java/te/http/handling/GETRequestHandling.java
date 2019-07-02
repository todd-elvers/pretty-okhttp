package te.http.handling;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import te.http.handling.error.exceptions.HttpClientException;
import te.http.handling.error.exceptions.HttpServerException;
import te.http.handling.error.exceptions.NoResponseException;

public interface GETRequestHandling {

    Headers getDefaultHeaders();
    HttpResponse executeRequest(Request request) throws HttpClientException, HttpServerException, NoResponseException;

    /**
     * Performs a generic GET request using the default headers.
     */
    default HttpResponse executeGET(String url) throws HttpClientException, HttpServerException, NoResponseException {
        return executeGET(url, null);
    }

    /**
     * URL encodes the provided parameters then performs a generic GET request using the default
     * headers.
     */
    default HttpResponse executeGET(String url, Map<String, ?> urlParams) throws HttpClientException, HttpServerException, NoResponseException {
        return executeRequest(buildRequestForGET(url, urlParams));
    }

    default Request buildRequestForGET(String url, @Nullable Map<String, ?> urlParams) {
        return new Request.Builder()
                .url(MapUtils.isEmpty(urlParams) ? url : addQueryParamsToURL(url, urlParams))
                .headers(getDefaultHeaders())
                .get()
                .build();
    }

    default String addQueryParamsToURL(String url, @Nonnull Map<String, ?> urlParams) {
        HttpUrl.Builder urlBuilder = HttpUrl.get(url).newBuilder();

        for (Map.Entry<String, ?> entry : urlParams.entrySet()) {
            String key = entry.getKey();
            String value = MapUtils.getString(urlParams, entry.getKey(), "");

            urlBuilder.addQueryParameter(key, value);
        }

        return urlBuilder.build().toString();
    }

}
