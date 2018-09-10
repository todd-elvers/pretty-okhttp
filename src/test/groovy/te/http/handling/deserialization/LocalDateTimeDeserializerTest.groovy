package te.http.handling.deserialization

import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.DateTimeDeserializationException

import java.time.LocalDateTime

class LocalDateTimeDeserializerTest extends Specification {

    @Subject
    LocalDateTimeDeserializer dateDeserializer = []

    def date = LocalDateTime.parse("2017-01-02T12:13:14")

    def "can handle timestamps"() {
        expect:
            dateDeserializer.parseDateString("2017-01-02T12:13:14") == date
    }

    def "throws LocalDateTimeParsingException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-date-string"

        when:
            dateDeserializer.parseDateString(dateString)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith("LocalDateTime")
            ex.message.contains(dateString)
            ex.message.contains("yyyy-MM-dd'T'HH:mm:ss")
    }

}
