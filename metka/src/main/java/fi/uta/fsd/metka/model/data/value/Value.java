package fi.uta.fsd.metka.model.data.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

/**
 * This contains only immutable value string and functions for checking its content and equality.
 * This is somewhat unnecessary and mostly here to strongly type value.
 * With this there's still the possibility to subclass value so that we have actual json numbers and booleans in the data instead of just strings.
 */
public class Value {
    private final String value;

    @JsonCreator
    public Value(@JsonProperty("value") String value) {
        this.value = value;
    }

    public String getValue() {
        return hasValue() ? value : "";
    }

    @JsonIgnore public boolean hasValue() {
        return StringUtils.hasText(value);
    }

    @JsonIgnore public boolean valueEquals(String compare) {
        return hasValue() && value.equals(compare);
    }

    public Value copy() {
        return new Value(value);
    }

    // TODO: Implement debugging friendly toString
}
