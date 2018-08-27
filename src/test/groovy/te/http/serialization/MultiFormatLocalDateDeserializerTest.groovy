package te.http.serialization

import spock.lang.Specification
import te.http.exceptions.LocalDateParsingException

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MultiFormatLocalDateDeserializerTest extends Specification {

    MultiFormatLocalDateDeserializer dateDeserializer = []

    def date = LocalDate.parse("2017-01-02")

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

    def "can handle unix epoch dates"() {
        given:
            def expectedLocalDate = Instant.ofEpochMilli(Long.parseLong("1508507424")).atZone(ZoneId.systemDefault()).toLocalDate()

        expect:
            dateDeserializer.parseDate("1508507424") == expectedLocalDate
    }


    def "throws DateParsingException if date cannot be handled"() {
        when:
            dateDeserializer.parseDate("sokmdf;alkdjf")

        then:
            thrown(LocalDateParsingException)
    }
}
