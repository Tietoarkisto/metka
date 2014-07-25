package fi.uta.fsd.metka.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerator for Configuration Field types.
 * Used to validate that type given in configuration file is a valid type.
 */
public enum FieldType {
    STRING(Values.STRING),
    INTEGER(Values.INTEGER),
    REAL(Values.REAL),
    BOOLEAN(Values.BOOLEAN),
    REFERENCE(Values.REFERENCE),
    CONTAINER(Values.CONTAINER, true),
    REFERENCECONTAINER(Values.REFERENCECONTAINER, true),
    SELECTION(Values.SELECTION),
    CONCAT(Values.CONCAT),
    DATE(Values.DATE),
    DATETIME(Values.DATETIME),
    TIME(Values.TIME);
    // Add more as needed

    private final String value;
    private final boolean container;

    public String getValue() {
        return value;
    }

    public boolean isContainer() {
        return container;
    }

    FieldType(String value) {
        this.value = value;
        this.container = false;
    }

    FieldType(String value, boolean container) {
        this.value = value;
        this.container = container;
    }

    @JsonCreator
    public static FieldType fromValue(String value) {
        for(FieldType type : values()) {
            if(type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public static boolean isValue(String value) {
        for(FieldType type : values()) {
            if(type.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Values {
        public static final String STRING = "STRING";
        public static final String INTEGER = "INTEGER";
        public static final String REAL = "REAL";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String REFERENCE = "REFERENCE";
        public static final String CONTAINER = "CONTAINER";
        public static final String REFERENCECONTAINER = "REFERENCECONTAINER";
        public static final String SELECTION = "SELECTION";
        public static final String CONCAT = "CONCAT";
        public static final String DATE = "DATE";
        public static final String DATETIME = "DATETIME";
        public static final String TIME = "TIME";
    }
}
