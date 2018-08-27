package te.http

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import te.http.handling.HttpRequestHandling
import te.http.handling.HttpResponse

class TestHelper {

    static HttpResponse buildEmpty200ResponseFor(Request request) {
        return buildResponse(request, 200, '{}')
    }

    static HttpResponse buildResponse(Request request, int statusCode, String content) {
        def responseBody = ResponseBody.create(HttpRequestHandling.applicationJSON, content)

        return new HttpResponse(
                new Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(statusCode)
                        .message("Mock response.")
                        .body(responseBody)
                        .build()
        )
    }

}
