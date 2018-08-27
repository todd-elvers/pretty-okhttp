package te.http.handling

import okhttp3.RequestBody
import spock.lang.Specification
import te.http.exceptions.Non200ResponseException
import te.http.exceptions.ServiceUnavailableException

class HttpRequestHandlingIntegrationTest extends Specification {
    def requestHandling = new HttpRequestHandling() {}

    def "when a service returns a 200 we build & return an HttpResponse"() {
        given: 'a request to POST to Google that will fail'
            def url = "https://www.google.com"
            def request = requestHandling.buildRequestForGET(url, null)

        when: 'we execute the POST'
            HttpResponse httpResponse = requestHandling.executeRequest(request)

        then: 'we return the response wrapped in our HttpResponse object'
            httpResponse
            println(httpResponse)
            httpResponse.wasSuccessful
            httpResponse.statusCode == 200
            !httpResponse.body.isEmpty()
            !httpResponse.statusMessage.isEmpty()
    }

    def "when a service returns a non-200 then a Non200ResponseException is thrown"() {
        given: 'a request to POST to Google that will fail'
            def url = "https://www.google.com"
            def someJSON = '{"some":"json"}'
            def request = requestHandling.buildRequestForPOST(url, RequestBody.create(requestHandling.applicationJSON, someJSON))

        when: 'we execute the POST'
            requestHandling.executeRequest(request)

        then: 'a non-200 exception is thrown that contains the real status code'
            def ex = thrown(Non200ResponseException)
            ex.httpResponse.statusCode == 405
            ex.httpResponse.statusMessage == "Method Not Allowed"
    }

    def "when a service does not respond then a ServiceUnavailableException is thrown"() {
        given: 'a request to POST to URL that does not exist'
            def url = "https://www.someurlthatprobablydoesnotexistkappakappaboingboing.com"
            def someJSON = '{"some":"json"}'
            def request = requestHandling.buildRequestForPOST(url, RequestBody.create(requestHandling.applicationJSON, someJSON))

        when: 'we execute the POST'
            requestHandling.executeRequest(request)

        then:
            thrown(ServiceUnavailableException)
    }

}
