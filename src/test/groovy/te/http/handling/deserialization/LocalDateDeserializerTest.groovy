package te.http.handling.deserialization

import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.DateTimeDeserializationException

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class LocalDateDeserializerTest extends Specification {

    @Subject
    LocalDateDeserializer dateDeserializer = []

    def date = LocalDate.parse("2017-01-02")

    def "can handle american dates w/ slashes"() {
        expect:
            dateDeserializer.parseDateString("01/02/2017") == date
    }

    def "can handle american dates w/ dashes"() {
        expect:
            dateDeserializer.parseDateString("01-02-2017") == date
    }

    def "can handle ISO 8601 dates"() {
        expect:
            dateDeserializer.parseDateString("2017-01-02") == date
    }

    def "can handle unix epoch dates"() {
        given:
            def expectedLocalDate = Instant.ofEpochMilli(Long.parseLong("1508507424")).atZone(ZoneId.systemDefault()).toLocalDate()

        expect:
            dateDeserializer.parseDateString("1508507424") == expectedLocalDate
    }


    def "throws LocalDateParsingException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-date-string"

        when:
            dateDeserializer.parseDateString(dateString)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith("LocalDate")
            ex.message.contains(dateString)
            ex.message.contains('yyyy-MM-dd')
            ex.message.contains('MM/dd/yyyy')
            ex.message.contains('MM-dd-yyyy')
    }
}
