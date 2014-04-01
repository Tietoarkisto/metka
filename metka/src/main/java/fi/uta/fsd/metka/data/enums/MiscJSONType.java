package fi.uta.fsd.metka.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MiscJSONType {
    ORGANISATION(Values.ORGANISATION),
    VOCABULARY(Values.VOCABULARY);

    private final String value;

    MiscJSONType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static MiscJSONType fromValue(String value) {
        for(MiscJSONType type : values()) {
            if(type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value);
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static boolean isType(String value) {
        for(MiscJSONType type : values()) {
            if(type.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static class Values {
        public static final String ORGANISATION = "ORGANISATION";
        public static final String VOCABULARY = "VOCABULARY";
    }
}
