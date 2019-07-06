package te.http.handling.json.deserialize.date

import com.google.gson.JsonPrimitive
import io.vavr.collection.List
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import te.http.handling.json.deserialize.date.DateTimeDeserializationException
import te.http.handling.json.deserialize.date.MultiFormatDateDeserializer
import te.http.handling.json.deserialize.date.parsing.DateParser
import te.http.handling.json.deserialize.date.parsing.JavaDateParser

import java.time.LocalDate
import java.time.LocalDateTime

class MultiFormatDateDeserializerTest extends Specification {

    @Shared
    @Subject
    def deserializerImpl = new MultiFormatDateDeserializer<Date>() {
        @Override
        List<DateParser> supportedFormats() {
            return List.of(
                    new JavaDateParser("MM/dd/yyyy", "[0-9]{2}/[0-9]{2}/[0-9]{4}")
            )
        }

        @Override
        Date fromUnixEpoch(long epoch) {
            return new Date(epoch)
        }
    }

    def "exceptions during deserialize() bubble up and are wrapped in a JsonParseException"() {
        given:
            def dateStringWrongFormat = "01-02-1234"
            def jsonElement = new JsonPrimitive(dateStringWrongFormat)

        when:
            deserializerImpl.deserialize(jsonElement, targetClass, null)

        then:
            def ex = thrown(DateTimeDeserializationException)
            ex.message.contains("${targetClass.getSimpleName()} deserialization failed for '01-02-1234'.  Tried Unix Epoch and the following formats: [MM/dd/yyyy].")

        where:
            targetClass << [Date, LocalDate, LocalDateTime]
    }

}
