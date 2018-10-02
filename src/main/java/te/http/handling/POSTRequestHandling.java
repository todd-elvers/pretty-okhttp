package te.http.handling;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import te.http.handling.exceptions.Non200ResponseException;
import te.http.handling.exceptions.ServiceUnavailableException;

public interface POSTRequestHandling {

    Headers getDefaultHeaders();
    HttpResponse executeRequest(Request request) throws Non200ResponseException, ServiceUnavailableException;

    /**
     * URL encodes the provided form data and then POSTs it w/ the application/x-www-form-urlencoded"
     * Content-Type header set.
     */
    default HttpResponse executeFormPOST(String url, Map<String, ?> formData) throws Non200ResponseException, ServiceUnavailableException {
        FormBody formBody = urlEncodeAsFormData(formData);
        Request request = buildRequestForPOST(url, formBody);

        return executeRequest(request);
    }

    /**
     * Performs a generic POST request using the provided content-type & default headers.
     *
     * @apiNote This does not handle "application/x-www-form-urlencoded" content, use {@link
     * #executeFormPOST(String, Map)} for that.
     */
    default HttpResponse executePOST(String url, MediaType contentType, String content) throws Non200ResponseException, ServiceUnavailableException {
        RequestBody requestBody = RequestBody.create(contentType, content.getBytes());
        Request request = buildRequestForPOST(url, requestBody);

        return executeRequest(request);
    }

    default Request buildRequestForPOST(String url, RequestBody body) {
        return new Request.Builder()
                .url(url)
                .headers(getDefaultHeaders())
                .post(body)
                .build();
    }

    default FormBody urlEncodeAsFormData(Map<String, ?> urlParams) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        urlParams.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> Objects.toString(entry.getValue(), "")
                        )
                )
                .forEach(formBodyBuilder::add);

        return formBodyBuilder.build();
    }

}