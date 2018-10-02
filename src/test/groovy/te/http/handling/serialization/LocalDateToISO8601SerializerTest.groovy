package te.http.handling.serialization

import com.google.gson.JsonPrimitive
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class LocalDateToISO8601SerializerTest extends Specification {

    @Shared
    @Subject
    LocalDateToISO8601Serializer dateSerializer = []

    def "formats a date in ISO8601 and wraps it in a JsonPrimitives"() {
        given:
            def input = LocalDate.parse("2017-01-02")
            def output = new JsonPrimitive("2017-01-02")

        expect:
            dateSerializer.serialize(input, null, null) == output
    }


}
