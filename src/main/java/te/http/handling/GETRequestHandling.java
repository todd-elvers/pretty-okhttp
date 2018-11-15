package te.http.handling;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        boolean isUrlParamsEmpty = urlParams == null || urlParams.size() == 0;

        return new Request.Builder()
                .url(isUrlParamsEmpty ? url : addQueryParamsToURL(url, urlParams))
                .headers(getDefaultHeaders())
                .get()
                .build();
    }

    default String addQueryParamsToURL(String url, @Nonnull Map<String, ?> urlParams) {
        HttpUrl parsedUrl = HttpUrl.parse(url);
        if (parsedUrl == null) {
            throw new RuntimeException("Could not parse " + url + " - URL appears to be malformed.");
        }

        HttpUrl.Builder urlBuilder = parsedUrl.newBuilder();
        urlParams.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> Objects.toString(entry.getValue(), "")
                        )
                )
                .forEach(urlBuilder::addQueryParameter);

        return urlBuilder.build().toString();
    }

}
