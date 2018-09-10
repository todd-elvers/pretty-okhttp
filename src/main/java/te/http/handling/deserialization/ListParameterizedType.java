package te.http.handling.deserialization;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import io.vavr.control.Try;

/**
 * This class allows us to preserve the type T of a list, which in turn allows us to generically retrieve lists
 * from JSON while hiding the odd trick we have to use to do it.
 *
 * How does this work?  It creates an implementation of Type as an anonymous class, which then allows us to
 * trap the type of T in there so we can retrieve it later.
 */
public class ListParameterizedType implements ParameterizedType {
    static boolean ex(){
        throw new RuntimeException("test");
    }
    public static void main(String[] args) {
        System.out.println(
                Try.of(ListParameterizedType::ex).toJavaOptional().isPresent()
        );
    }
    private Type type;

    public ListParameterizedType(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{type};
    }

    @Override
    public Type getRawType() {
        return ArrayList.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
