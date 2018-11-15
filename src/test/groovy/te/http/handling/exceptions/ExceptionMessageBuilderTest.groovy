package te.http.handling.exceptions

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import spock.lang.Specification
import spock.lang.Unroll
import te.http.handling.HttpRequestHandling
import te.http.handling.HttpResponse
import te.http.handling.error.ExceptionMessageBuilder
import te.http.handling.error.TimeoutDetector

class ExceptionMessageBuilderTest extends Specification {

    ExceptionMessageBuilder exceptionMessageBuilder = [
            timeoutDetector: Mock(TimeoutDetector)
    ]

    @Unroll
    def "when service doesn't respond due to #condition then indicates this in the message"() {
        given:
            def request = new Request.Builder()
                    .url("https://www.google.com")
                    .header("Spaghetti", 'forever')
                    .get()
                    .build()

        when:
            exceptionMessageBuilder.timeoutDetector.isConnectTimeout(_) >> isConnectTimeout
            exceptionMessageBuilder.timeoutDetector.isReadTimeout(_) >> isReadTimeout

        and:
            def message = exceptionMessageBuilder.buildNoResponseMessage(
                    new Exception(),
                    request
            )

        then:
            message == """\
                $messageHeader
                \tURL = https://www.google.com/
                \tMethod = GET
            """.stripIndent()

        where:
            condition               | isConnectTimeout | isReadTimeout || messageHeader
            'connect timeout'       | true             | false         || 'Connect timeout occurred!'
            'read timeout'          | false            | true          || 'Read timeout occurred!'
            'some unexpected error' | false            | false         || 'Service is unavailable or unresponsive!'
    }

    def "when a non-200 occurs build a detailed message"() {
        given:
            def request = new Request.Builder()
                    .url("https://www.google.com")
                    .header("Spaghetti", 'request')
                    .get()
                    .build()

        and:
            def httpResponse = new HttpResponse(
                    new Response.Builder()
                            .request(request)
                            .code(404)
                            .body(ResponseBody.create(HttpRequestHandling.applicationJSON, "payload"))
                            .header("Spaghetti", 'response')
                            .message("Not Found")
                            .protocol(Protocol.HTTP_1_1)
                            .build()
            )

        when:
            def message = exceptionMessageBuilder.buildNon200ResponseMessage(httpResponse)

        then:
            message == """\
            Request returned non-200!
            \tURL = https://www.google.com/
            \tMethod = GET
            \tCode = 404
            \tMessage = Not Found
            \tBody = payload
            """.stripIndent()
    }

}
