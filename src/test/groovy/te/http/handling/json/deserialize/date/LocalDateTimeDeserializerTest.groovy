package te.http.handling.json.deserialize.date

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class LocalDateTimeDeserializerTest extends Specification {

    @Shared
    @Subject
    LocalDateTimeDeserializer deserializer = []

    @Shared
    LocalDateTime expectedDate = LocalDateTime.parse("2017-01-02T12:13:14")

    def "can handle timestamps"() {
        when:
            LocalDateTime date = deserializer.parseDateString("2017-01-02T12:13:14", LocalDateTime)

        then:
            date == expectedDate
    }

    def "throws LocalDateTimeParsingException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-expectedDate-string"

        when:
            deserializer.parseDateString(dateString, LocalDateTime)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith(LocalDateTime.name)
            ex.message.contains(dateString)
            ex.message.contains("yyyy-MM-dd'T'HH:mm:ss")
    }

}
