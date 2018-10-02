package te.http.handling

import spock.lang.Specification

class HttpResponseTest extends Specification {

    def "has a no-arg constructor and a fluent interface"() {
        expect:
            new HttpResponse()
                    .setBody("{}")
                    .setSuccessful()
                    .setStatusCode(200)
                    .setStatusMessage("OK")
    }

}
