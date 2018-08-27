package te.http.handling

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import spock.lang.Shared
import spock.lang.Specification
import te.http.handling.JsonMarshalling

class JsonMarshallingTest extends Specification {

    @Shared
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


    static class Person {
        String firstName
        String lastName
        @SerializedName("years_old")
        int age
    }
}
