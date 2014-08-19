package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.Map;

public class ValueDataField extends DataField {
    /**
     * Builder method
     * @param key
     * @return
     */
    public static ValueDataField build(String key) {
        return new ValueDataField(key, DataFieldType.VALUE);
    }

    private final Map<Language, ValueContainer> original = new HashMap<>();
    private final Map<Language, ValueContainer> current = new HashMap<>();

    @JsonCreator
    public ValueDataField(@JsonProperty("key") String key, @JsonProperty("type") DataFieldType type) {
        // Let's just assume that polymorphism works and that the given type is a correct type
        super(key, type);
    }

    public Map<Language, ValueContainer> getOriginal() {
        return original;
    }

    public Map<Language, ValueContainer> getCurrent() {
        return current;
    }

    public ValueContainer getOriginalFor(Language language) {
        return original.get(language);
    }

    public void setOriginalFor(Language language, ValueContainer originalValue) {
        original.put(language, originalValue);
    }

    public ValueContainer getCurrentFor(Language language) {
        return current.get(language);
    }

    public void setCurrentFor(Language language, ValueContainer currentValue) {
        current.put(language, currentValue);
    }

    /**
     * Convenience method for getting an up to date value.
     * If there exists a modified value then return that, otherwise return original value.
     * @return SavedValue, either modified value if exists or original value. Can return null if both are null.
     */
    @JsonIgnore
    public ValueContainer getValueFor(Language language) {
        return (getCurrentFor(language) != null) ? current.get(language) : original.get(language);
    }

    /**
     * Convenience method for returning the actual value in this ValueDataField.
     * NOTICE: Returns empty string if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or empty string if value doesn't exist
     */
    @JsonIgnore
    public String getActualValueFor(Language language) {
        String value = "";
        if(hasValueFor(language)) {
            value = getValueFor(language).getActualValue();
        }
        return value;
    }

    public boolean hasOriginalFor(Language language) {
        return getOriginalFor(language) != null;
    }

    public boolean hasCurrentFor(Language language) {
        return getCurrentFor(language) != null;
    }

    /**
     * Convenience method to check if there exists a ValueContainer object for given language
     * and if that object actually has content.
     * @param language
     * @return
     */
    public boolean hasValueFor(Language language) {
        return getValueFor(language) != null && getValueFor(language).hasValue();
    }

    @JsonIgnore
    public boolean originalForEquals(Language language, String compare) {
        return hasOriginalFor(language) && getOriginalFor(language).valueEquals(compare);
    }

    @JsonIgnore
    public boolean currentForEquals(Language language, String compare) {
        return hasCurrentFor(language) && getCurrentFor(language).valueEquals(compare);
    }

    /**
     * Checks if current and original values for given language are equal.
     * NOTICE: Unlike other equality checks this assumes that null values are infact equal
     * @param language
     * @return
     */
    @JsonIgnore
    public boolean currentForEqualsOriginal(Language language) {
        if(hasCurrentFor(language) != hasOriginalFor(language)) {
            return false;
        }
        if(!hasCurrentFor(language) && !hasOriginalFor(language)) {
            return true;
        } else {
            return getCurrentFor(language).valueEquals(getOriginalFor(language).getActualValue());
        }
    }

    /**
     * Convenience method for checking if the most recent value on this ValueDataField equals the given value.
     * NOTICE: Returns false if there is no value since null values should not be equal by default.
     *
     * @param compare - Value to compare
     * @return True if the value in this ValueDataField equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueForEquals(Language language, String compare) {
        return hasValueFor(language) && getValueFor(language).valueEquals(compare);
    }

    @Override
    public DataField copy() {
        ValueDataField field = new ValueDataField(getKey(), getType());
        for(Map.Entry<Language, ValueContainer> e : original.entrySet()) {
            field.original.entrySet().add(e);
        }
        for(Map.Entry<Language, ValueContainer> e : current.entrySet()) {
            field.current.entrySet().add(e);
        }
        return field;
    }

    @Override
    public void normalize() {
        // If there's no modified value then don't do anything
        for(Language language : Language.values()) {
            if(current.get(language) != null) {
                original.put(language, current.get(language));
                current.remove(language);
            }
        }
    }
}
