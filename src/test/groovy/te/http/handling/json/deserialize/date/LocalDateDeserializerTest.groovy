package te.http.handling.json.deserialize.date

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import te.http.TestUtils

import java.time.LocalDate

class LocalDateDeserializerTest extends Specification {

    @Subject
    @Shared
    LocalDateDeserializer deserializer = []

    @Shared
    LocalDate expectedDate = LocalDate.parse("2017-01-02")

    @Unroll("can handle #format format")
    void "can handle the the expected formats"() {
        expect:
            deserializer.parseDateString(dateString, Date) == expectedDate

        where:
            format       | dateString
            "unix epoch" | TestUtils.newDate("01/02/2017").time.toString()
            "ISO-8601"   | "2017-01-02"
            "MM/dd/yyyy" | "01/02/2017"
            "MM-dd-yyyy" | "01-02-2017"
    }


    def "throws LocalDateParsingException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-expectedDate-string"

        when:
            deserializer.parseDateString(dateString, LocalDate)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith(LocalDate.name)
            ex.message.contains(dateString)
            ex.message.contains('yyyy-MM-dd')
            ex.message.contains('MM/dd/yyyy')
            ex.message.contains('MM-dd-yyyy')
    }
}
