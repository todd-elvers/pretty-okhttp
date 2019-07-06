package te.http.handling.json.deserialize.date

import org.apache.commons.lang3.time.FastDateFormat
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import te.http.TestUtils

class JavaDateDeserializerTest extends Specification {

    @Subject
    @Shared
    JavaDateDeserializer deserializer = []

    @Shared
    Date expectedDate = FastDateFormat.getInstance("MM/dd/yyyy").parse("01/02/2017")

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


    def "throws DateTimeDeserializationException with the correct message if date cannot be handled"() {
        given:
            String dateString = "some-expectedDate-string"

        when:
            deserializer.parseDateString(dateString, Date)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.startsWith(Date.name)
            ex.message.contains(dateString)
            ex.message.contains('yyyy-MM-dd')
            ex.message.contains('MM/dd/yyyy')
            ex.message.contains('MM-dd-yyyy')
    }
}
