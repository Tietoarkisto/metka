package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.stringToLong;

/**
 * Immutable class to combine DateTimeUserPair and Value to one object.
 * This class and all of its components are immutable so the usage has to be clear.
 * You can't just change a value without having to insert new save info as well or
 * at least without using a copy of save info from somewhere.
 */
public class ValueContainer {
    /**
     * Creates and initialises a new SavedValue from given parameters.
     * @param info Save info for the ValueContainer in the form of DateTimeUserPair.
     * @param value Value to be inserted into the new ValueContainer
     * @return Initialised ValueContainer ready for use
     */
    public static ValueContainer build(DateTimeUserPair info, Value value) {
        return new ValueContainer(info, value);
    }

    private final DateTimeUserPair saved;
    private final Value value;

    @JsonCreator
    public ValueContainer(@JsonProperty("saved") DateTimeUserPair saved, @JsonProperty("value") Value value) {
        this.saved = saved != null ? saved : new DateTimeUserPair(null, null);
        this.value = value;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+"]";
    }

    @JsonIgnore public boolean hasValue() {
        if(value == null) {
            return false;
        } else {
            return value.hasValue();
        }
    }

    @JsonIgnore
    public String getActualValue() {
        if(hasValue()) {
            return value.getValue();
        } else {
            return "";
        }
    }

    /**
     * If there is a value then return the value portion as a Long.
     * INTEGER type is a description, not an implementation requirement.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getActualValue() method on to get value.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed Long.
     *          This can return null.
     * @return Long gained by using ConversionUtil.stringToLong on the simple value representation if value is found.
     */
    @JsonIgnore public Long valueAsInteger() {
        return stringToLong(getActualValue());
    }

    @JsonIgnore public boolean valueAsBoolean() {
        return Boolean.parseBoolean(getActualValue());
    }

    @JsonIgnore public boolean valueEquals(String compare) {
        return hasValue() && value.valueEquals(compare);
    }

    public ValueContainer copy() {
        return new ValueContainer(saved.copy(), value.copy());
    }
}
