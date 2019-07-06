package te.http.handling.json.serialize.date

import com.google.gson.JsonPrimitive
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.TestUtils
import te.http.handling.json.serialize.date.JavaDateToISO8601Serializer

class JavaDateToISO8601SerializerTest extends Specification {

    @Shared
    @Subject
    JavaDateToISO8601Serializer dateSerializer = []

    def "formats a date in ISO8601 and wraps it in a JsonPrimitives"() {
        given:
            def input = TestUtils.newDate("01/02/2017")
            def output = new JsonPrimitive("2017-01-02")

        expect:
            dateSerializer.serialize(input, null, null) == output
    }

}
