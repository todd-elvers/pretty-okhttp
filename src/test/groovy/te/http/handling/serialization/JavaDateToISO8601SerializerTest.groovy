package te.http.handling.serialization

import com.google.gson.JsonPrimitive
import spock.lang.Specification

class JavaDateToISO8601SerializerTest extends Specification {

    JavaDateToISO8601Serializer dateSerializer = []

    def "formats a date in ISO8601 and wraps it in a JsonPrimitives"() {
        given:
            def input = Date.parse("MM/dd/yyyy", "01/02/2017")
            def output = new JsonPrimitive("2017-01-02")

        expect:
             dateSerializer.serialize(input, null, null) == output
    }

}
