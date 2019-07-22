package te.http.handling

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.apache.commons.lang3.RandomUtils
import org.junit.Rule
import org.mockserver.junit.MockServerRule
import org.mockserver.model.HttpStatusCode
import spock.lang.Retry
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import te.http.handling.error.HttpClientException
import te.http.handling.error.HttpServerException
import te.http.handling.error.NoResponseException

import java.util.concurrent.TimeUnit

import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.notFoundResponse
import static org.mockserver.model.HttpResponse.response

class HttpRequestHandlingIntegrationTest extends Specification {

    @Rule
    MockServerRule webServer = new MockServerRule(this, 8888)

    @Shared
    @Subject
    HttpRequestHandling requestHandling = new HttpRequestHandling() {}

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
            httpResponse.is200()
            httpResponse.statusCode == 200
            httpResponse.getBodyAsString().get() == json
            !httpResponse.statusMessage.isEmpty()
    }

    def "when a service returns a 400 then a HttpClientException is thrown"() {
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

        then: 'a HttpClientException is thrown that contains the real status code'
            def ex = thrown(HttpClientException)
            ex.httpResponse.statusCode == 404
            ex.httpResponse.statusMessage == "Not Found"
    }

    def "when a service returns a 500 then a HttpServerException is thrown"() {
        given:
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(
                            response()
                                    .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR_500.code())
                                    .withReasonPhrase(HttpStatusCode.INTERNAL_SERVER_ERROR_500.reasonPhrase())
                                    .withBody("payload")
                    )

        when:
            requestHandling.executeGET(url)

        then:
            def exception = thrown(HttpServerException)

        and: 'correctly parsed the response'
            exception.httpResponse.statusMessage == HttpStatusCode.INTERNAL_SERVER_ERROR_500.reasonPhrase()
            exception.httpResponse.bodyAsString.get() == "payload"

        and: 'the exception message was as detailed as possible'
            exception.message == """\
                Request returned 500!
                \tURL = $url
                \tMethod = GET
                \tMessage = $exception.httpResponse.statusMessage
                \tBody = payload
            """.stripIndent()

    }

    @Unroll
    def "#non200Response.code()'s cause no exception when isNon200Exceptional() returns false"() {
        given:
            HttpRequestHandling requestHandling = new HttpRequestHandling() {
                @Override
                boolean shouldThrowExceptionForNon200() {
                    return false
                }
            }

        and:
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(
                            response()
                                    .withStatusCode(non200Response.code())
                                    .withReasonPhrase(non200Response.reasonPhrase())
                                    .withBody("payload")
                    )

        when:
            requestHandling.executeGET(url)

        then:
            noExceptionThrown()

        where:
            non200Response << [
                    HttpStatusCode.INTERNAL_SERVER_ERROR_500,
                    HttpStatusCode.BAD_REQUEST_400
            ]
    }

    def "when a service does not respond then a NoResponseException is thrown"() {
        given: 'a request to POST to URL that does not exist'
            def url = "https://www.someurlthatprobablydoesnotexistkappakappaboingboing.com"
            def request = requestHandling.buildRequestForPOST(
                    url,
                    RequestBody.create(requestHandling.applicationJSON, json)
            )

        when: 'we execute the POST'
            requestHandling.executeRequest(request)

        then:
            thrown(NoResponseException)
    }

    @Retry
    def "can correctly identify connect timeouts"() {
        given:
            HttpRequestHandling requestHandlingWithShortTimeouts = new HttpRequestHandling() {
                @Override
                OkHttpClient getHttpClient() {
                    return new OkHttpClient.Builder()
                            .readTimeout(1, TimeUnit.MILLISECONDS)
                            .connectTimeout(1, TimeUnit.MILLISECONDS)
                            .build()
                }
            }

        and: 'a non-routable IP address that will throw a connect timeout (most of the time)'
            String url = "http://10.255.255.1"

        when:
            requestHandlingWithShortTimeouts.executeGET(url)

        then:
            def exception = thrown(NoResponseException)

        and: 'we correctly detected a connect timeout'
            exception.isConnectTimeout()

        and: 'we did not detect a read timeout'
            !exception.isReadTimeout()
    }

    @Retry
    def "can correctly identify read timeouts"() {
        given:
            HttpRequestHandling requestHandlingWithShortTimeouts = new HttpRequestHandling() {
                @Override
                OkHttpClient getHttpClient() {
                    return new OkHttpClient.Builder()
                            .readTimeout(1, TimeUnit.MILLISECONDS)
                            .connectTimeout(1, TimeUnit.MILLISECONDS)
                            .build()
                }
            }

        and: 'a web server that takes 5 seconds to return'
            webServer.getClient()
                    .when(request().withPath(uri))
                    .respond(response().withDelay(TimeUnit.SECONDS, 5))

        when:
            requestHandlingWithShortTimeouts.executeGET(url)

        then:
            def exception = thrown(NoResponseException)

        and: 'we correctly detected a read timeout'
            !exception.isConnectTimeout()

        and: 'we did not detect a connect timeout'
            exception.isReadTimeout()
    }
}
