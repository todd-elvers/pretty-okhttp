package te.http.handling.deserialization

import spock.lang.Specification
import spock.lang.Subject

import te.http.handling.exceptions.DateTimeDeserializationException

class JavaDateDeserializerTest extends Specification {

    @Subject
    JavaDateDeserializer dateDeserializer = []

    def date = Date.parse("MM/dd/yyyy", "01/02/2017")

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

    def "can handle Unix Epoch dates"() {
        expect:
            dateDeserializer.parseDateString("1508507424") == new Date(1508507424L)
    }

    def "throws DateTimeDeserializationException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-date-string"

        when:
            dateDeserializer.parseDateString(dateString)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith("Date")
            ex.message.contains(dateString)
            ex.message.contains('yyyy-MM-dd')
            ex.message.contains('MM/dd/yyyy')
            ex.message.contains('MM-dd-yyyy')
    }
}
