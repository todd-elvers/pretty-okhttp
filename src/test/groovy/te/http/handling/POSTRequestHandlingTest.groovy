package te.http.handling

import groovy.util.logging.Slf4j
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.Non200ResponseException

import static te.http.TestHelper.buildEmpty200ResponseFor

@Slf4j
class POSTRequestHandlingTest extends Specification {

    @Shared
    @Subject
    def requestHandling = new POSTRequestHandling() {
        @Override
        HttpResponse executeRequest(Request request) throws Non200ResponseException {
            return buildEmpty200ResponseFor(request)
        }

        @Override
        Headers getDefaultHeaders() {
            return new Headers.Builder().add("Accept", "text/html").build()
        }
    }

    def "executeFormPOST() & executePOST() build & POST requests appropriately"() {
        given:
            def url = 'https://www.spaghetti-inc.spaghetti/'

        when:
            def responseOfPOST = requestHandling.executePOST(url, HttpRequestHandling.applicationJSON, '{}')
            def responseOfFormPOST = requestHandling.executeFormPOST(url, new HashMap<String, Object>())

        then:
            responseOfPOST.statusCode == 200
            responseOfPOST.statusMessage
            responseOfPOST.originalRequest.url().toString() == url
            responseOfPOST.body == "{}"

        and:
            responseOfFormPOST.statusCode == 200
            responseOfFormPOST.statusMessage
            responseOfFormPOST.originalRequest.url().toString() == url
            responseOfFormPOST.body == "{}"
    }


    def "no exception is thrown when form-encoding params contain a null value"() {
        when:
            FormBody formBody = requestHandling.urlEncodeAsFormData([firstKey: 'firstValue', secondKey: null])

        then:
            noExceptionThrown()

        and:
            formBody.name(0) == 'firstKey'
            formBody.value(0) == 'firstValue'
            formBody.name(1) == 'secondKey'
            formBody.value(1) == ''
    }

    def "buildRequestForPOST() builds a POST request with the default headers"() {
        given:
            RequestBody requestBody = RequestBody.create(HttpRequestHandling.applicationJSON, "{}")

        when:
            Request request = requestHandling.buildRequestForPOST("http://www.google.com/", requestBody)

        then:
            request.method() == "POST"
            request.headers() == requestHandling.getDefaultHeaders()
            request.url().toString() == "http://www.google.com/"
    }

}
