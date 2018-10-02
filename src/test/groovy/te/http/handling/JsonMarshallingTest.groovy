package te.http.handling


import com.google.gson.annotations.SerializedName
import groovy.transform.EqualsAndHashCode
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class JsonMarshallingTest extends Specification {

    @Shared
    @Subject
    JsonMarshalling jsonMarshalling = new JsonMarshalling() {}

    def "can serialize an object into JSON"() {
        given:
            def person = new Person(firstName: "Todd", lastName: "Elvers", age: 27)

        expect:
            jsonMarshalling.toJson(person) == '{"firstName":"Todd","lastName":"Elvers","years_old":27}'
    }

    def "can deserialize JSON into an object"() {
        given:
            def json = '{"firstName":"Todd","lastName":"Elvers"}'

        when:
            Person result = jsonMarshalling.fromJson(json, Person)

        then:
            result
            result.firstName == "Todd"
            result.lastName == "Elvers"
    }

    def "can deserialize JSON into a list of objects"() {
        given:
            def json = """
                [
                    {"firstName":"Todd", "lastName":"Elvers", "years_old": 28},
                    {"firstName":"Missy", "lastName":"Williams", "years_old": 27}
                ]
            """

        when:
            List<Person> results = jsonMarshalling.fromJsonList(json, Person)

        then:
            results
            results[0].firstName == "Todd"
            results[0].lastName == "Elvers"
            results[0].age == 28
            results[1].firstName == "Missy"
            results[1].lastName == "Williams"
            results[1].age == 27
    }

    def "can serialize/deserialize Date, LocalDate, and LocalDateTime to/from JSON"() {
        given:
            ObjectWithDates original = new ObjectWithDates()
            String originalJSON = jsonMarshalling.toJson(original)

        when:
            ObjectWithDates recreated = jsonMarshalling.fromJson(
                    originalJSON,
                    ObjectWithDates.class
            )

        then:
            noExceptionThrown()

        and:
            original == recreated
    }

    static class Person {
        String firstName
        String lastName

        @SerializedName("years_old")
        int age
    }

    @EqualsAndHashCode
    static class ObjectWithDates {
        Date javaDate = Date.parse("MM/dd/yyyy", "09/10/2018")
        LocalDate localDate = LocalDate.parse("2018-09-10")
        LocalDateTime localDateTime = LocalDateTime.parse("2018-09-10T00:00:01")
        LocalDateTime localDateTimeEdgeCase = LocalDateTime.parse("2018-09-10T00:00:00")
    }
}
