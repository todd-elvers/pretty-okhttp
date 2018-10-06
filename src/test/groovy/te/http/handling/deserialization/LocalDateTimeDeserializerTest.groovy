package te.http.handling.deserialization

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.DateTimeDeserializationException

import java.time.LocalDateTime

class LocalDateTimeDeserializerTest extends Specification {

    @Shared
    @Subject
    LocalDateTimeDeserializer dateDeserializer = []

    def expectedDate = LocalDateTime.parse("2017-01-02T12:13:14")

    def "can handle timestamps"() {
        expect:
            dateDeserializer
                    .parseDateString("2017-01-02T12:13:14", LocalDateTime) == expectedDate
    }

    def "throws LocalDateTimeParsingException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-date-string"

        when:
            dateDeserializer.parseDateString(dateString, LocalDateTime)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith(LocalDateTime.name)
            ex.message.contains(dateString)
            ex.message.contains("yyyy-MM-dd'T'HH:mm:ss")
    }

}
