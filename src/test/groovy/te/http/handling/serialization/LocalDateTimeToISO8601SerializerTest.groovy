package te.http.handling.serialization

import com.google.gson.JsonPrimitive
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class LocalDateTimeToISO8601SerializerTest extends Specification {

    @Subject
    LocalDateTimeToISO8601Serializer dateSerializer = []

    def date = LocalDateTime.parse("2017-01-02T12:13:14")

    def "formats a date in ISO8601 and wraps it in a JsonPrimitives"() {
        given:
            def output = new JsonPrimitive(date.toString())

        expect:
            dateSerializer.serialize(date, null, null) == output
    }

}
