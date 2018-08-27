package te.http.serialization

import spock.lang.Specification
import te.http.exceptions.DateParsingException
import te.http.serialization.MultiFormatDateDeserializer

class MultiFormatDateDeserializerTest extends Specification {

    MultiFormatDateDeserializer dateDeserializer = []

    def date = Date.parse("MM/dd/yyyy", "01/02/2017")

    def "can handle american dates w/ slashes"() {
        expect:
            dateDeserializer.parseDate("01/02/2017") == date
    }

    def "can handle american dates w/ dashes"() {
        expect:
            dateDeserializer.parseDate("01-02-2017") == date
    }

    def "can handle ISO 8601 dates"() {
        expect:
            dateDeserializer.parseDate("2017-01-02") == date
    }

    def "can handle Unix Epoch dates"() {
        expect:
            dateDeserializer.parseDate("1508507424") == new Date(1508507424L)
    }

    def "throws DateParsingException if date cannot be handled"() {
        when:
            dateDeserializer.parseDate("sokmdf;alkdjf")

        then:
            thrown(DateParsingException)
    }
}
