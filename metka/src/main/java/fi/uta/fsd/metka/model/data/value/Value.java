package fi.uta.fsd.metka.model.data.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class Value {
    private final String value;
    private final String derived;

    public Value(String value) {
        this.value = value;
        this.derived = "";
    }

    @JsonCreator
    public Value(@JsonProperty("value") String value, @JsonProperty("derived") String derived) {
        this.value = value;
        this.derived = derived;
    }

    public String getValue() {
        return hasValue() ? value : "";
    }

    public String getDerived() {
        return hasDerived() ? derived : "";
    }

    @JsonIgnore public boolean hasValue() {
        return StringUtils.hasText(value);
    }

    @JsonIgnore public boolean hasDerived() {
        return StringUtils.hasText(derived);
    }

    @JsonIgnore public boolean valueEquals(String compare) {
        return hasValue() && value.equals(compare);
    }

    @JsonIgnore public boolean derivedEquals(String compare) {
        return hasDerived() && derived.equals(compare);
    }

    public Value copy() {
        return new Value(value, derived);
    }

    // TODO: Implement debugging friendly toString
}
