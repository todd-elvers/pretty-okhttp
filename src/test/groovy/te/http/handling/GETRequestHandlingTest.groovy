package te.http.handling

import okhttp3.Headers
import okhttp3.Request
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.error.HttpClientException

import static te.http.TestHelper.buildEmpty200ResponseFor

class GETRequestHandlingTest extends Specification {

    @Shared
    @Subject
    def requestHandling = new GETRequestHandling() {
        @Override
        HttpResponse executeRequest(Request request) throws HttpClientException {
            return buildEmpty200ResponseFor(request)
        }

        @Override
        Headers getDefaultHeaders() {
            return new Headers.Builder().add("Accept", "text/html").build()
        }
    }

    def "urlParams are not required"() {
        when:
            requestHandling.executeGET("http://www.google.com")

        then:
            noExceptionThrown()
    }

    def "no exception is thrown when URL params are empty, null, or have a null value"() {
        when:
            requestHandling.executeGET("http://www.google.com", urlParams)

        then:
            noExceptionThrown()

        where:
            urlParams << [
                    [:],
                    null,
                    [something: null]
            ]
    }

    def "buildRequestForGET() builds a GET request with the default headers"() {
        when:
            Request request = requestHandling.buildRequestForGET("http://www.google.com/", urlParams)

        then:
            request.method() == "GET"
            request.headers() == requestHandling.getDefaultHeaders()
            request.url().toString() == "http://www.google.com/" + queryString

        where:
            urlParams                    | queryString
            null                         | ''
            [:]                          | ''
            [apple: true, banana: false] | '?apple=true&banana=false'
    }
}
