package te.http.handling.deserialization

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.exceptions.DateTimeDeserializationException

class JavaDateDeserializerTest extends Specification {

    @Shared
    @Subject
    JavaDateDeserializer dateDeserializer = []

    def date = Date.parse("MM/dd/yyyy", "01/02/2017")

    def "can handle american dates w/ slashes"() {
        expect:
            dateDeserializer.parseDateString("01/02/2017", Date) == date
    }

    def "can handle american dates w/ dashes"() {
        expect:
            dateDeserializer.parseDateString("01-02-2017", Date) == date
    }

    def "can handle ISO 8601 dates"() {
        expect:
            dateDeserializer.parseDateString("2017-01-02", Date) == date
    }

    def "can handle Unix Epoch dates"() {
        expect:
            dateDeserializer.parseDateString("1508507424", Date) == new Date(1508507424L)
    }

    def "throws DateTimeDeserializationException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-date-string"

        when:
            dateDeserializer.parseDateString(dateString, Date)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith(Date.name)
            ex.message.contains(dateString)
            ex.message.contains('yyyy-MM-dd')
            ex.message.contains('MM/dd/yyyy')
            ex.message.contains('MM-dd-yyyy')
    }
}
