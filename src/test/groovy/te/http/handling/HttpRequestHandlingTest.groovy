package te.http.handling

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.apache.commons.lang3.RandomUtils
import org.junit.Rule
import org.mockserver.junit.MockServerRule
import spock.lang.Retry
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.Non200ResponseException
import te.http.handling.exceptions.ServiceUnavailableException

import java.util.concurrent.TimeUnit

import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.notFoundResponse
import static org.mockserver.model.HttpResponse.response

class HttpRequestHandlingTest extends Specification {

    @Rule
    MockServerRule webServer = new MockServerRule(this)

    @Shared
    @Subject
    HttpRequestHandling requestHandling = new HttpRequestHandling() {}

    @Shared
    @Subject
    HttpRequestHandling requestHandlingWithShortTimeouts = new HttpRequestHandling() {
        @Override
        OkHttpClient getHttpClient() {
            return new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.MILLISECONDS)
                    .connectTimeout(1, TimeUnit.MILLISECONDS)
                    .build()
        }
    }

    String uri = "/test/uri/${RandomUtils.nextLong(1, 1_000_000)}"
    String url = "http://localhost:${webServer.port}${uri}"
    String json = '{"some":"json"}'

    def "when a service returns a 200 we build & return an HttpResponse"() {
        given: 'a web server that always returns 200'
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(response(json))

        and: 'a GET request to that web server'
            def request = requestHandling.buildRequestForGET(url, null)

        when: 'we execute the GET'
            HttpResponse httpResponse = requestHandling.executeRequest(request)

        then: 'we return the response wrapped in our HttpResponse object'
            httpResponse
            httpResponse.wasSuccessful()
            httpResponse.statusCode == 200
            httpResponse.getBody() == json
            !httpResponse.statusMessage.isEmpty()
    }

    def "when a service returns a non-200 then a Non200ResponseException is thrown"() {
        given: 'a web server that always returns 404'
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(notFoundResponse())

        and: 'a POST request to that web server'
            def request = requestHandling.buildRequestForPOST(
                    url,
                    RequestBody.create(requestHandling.applicationJSON, json)
            )

        when: 'we execute the POST'
            requestHandling.executeRequest(request)

        then: 'a non-200 exception is thrown that contains the real status code'
            def ex = thrown(Non200ResponseException)
            ex.httpResponse.statusCode == 404
            ex.httpResponse.statusMessage == "Not Found"
    }

    def "when a service does not respond then a ServiceUnavailableException is thrown"() {
        given: 'a request to POST to URL that does not exist'
            def url = "https://www.someurlthatprobablydoesnotexistkappakappaboingboing.com"
            def request = requestHandling.buildRequestForPOST(
                    url,
                    RequestBody.create(requestHandling.applicationJSON, json)
            )

        when: 'we execute the POST'
            requestHandling.executeRequest(request)

        then:
            thrown(ServiceUnavailableException)
    }

    @Retry
    def "can correctly identify connect timeouts"() {
        given: 'a non-routable IP address that will throw a connect timeout (most of the time)'
            String url = "http://10.255.255.1"

        when:
            requestHandlingWithShortTimeouts.executeGET(url)

        then:
            def exception = thrown(ServiceUnavailableException)

        and: 'we correctly detected a connect timeout'
            exception.wasConnectTimeout()

        and: 'we did not detect a read timeout'
            !exception.wasReadTimeout()
    }

    def "can correctly identify read timeouts"() {
        given: 'an web server that takes 5 seconds to return'
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(response().withDelay(TimeUnit.SECONDS, 5))

        when:
            requestHandlingWithShortTimeouts.executeGET(url)

        then:
            def exception = thrown(ServiceUnavailableException)

        and: 'we correctly detected a read timeout'
            !exception.wasConnectTimeout()

        and: 'we did not detect a connect timeout'
            exception.wasReadTimeout()
    }
}
