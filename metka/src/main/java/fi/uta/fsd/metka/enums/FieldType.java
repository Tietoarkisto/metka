package fi.uta.fsd.metka.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerator for Configuration Field types.
 * Used to validate that type given in configuration file is a valid type.
 */
public enum FieldType {
    STRING(Values.STRING, true),
    INTEGER(Values.INTEGER, true),
    REAL(Values.REAL, true),
    BOOLEAN(Values.BOOLEAN),
    REFERENCE(Values.REFERENCE),
    CONTAINER(Values.CONTAINER, false, true),
    REFERENCECONTAINER(Values.REFERENCECONTAINER, false, true),
    SELECTION(Values.SELECTION),
    CONCAT(Values.CONCAT),
    DATE(Values.DATE, true),
    DATETIME(Values.DATETIME, true),
    TIME(Values.TIME, true),
    RICHTEXT(Values.RICHTEXT, true);
    // Add more as needed

    private final String value;
    private final boolean container;
    private final boolean canBeFreeText;

    public String getValue() {
        return value;
    }

    public boolean isContainer() {
        return container;
    }

    public boolean isCanBeFreeText() {
        return canBeFreeText;
    }

    FieldType(String value) {
        this.value = value;
        this.canBeFreeText = false;
        this.container = false;
    }

    FieldType(String value, boolean canBeFreeText) {
        this.value = value;
        this.canBeFreeText = canBeFreeText;
        this.container = false;
    }

    FieldType(String value, boolean canBeFreeText, boolean container) {
        this.value = value;
        this.canBeFreeText = canBeFreeText;
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
        public static final java.lang.String RICHTEXT = "RICHTEXT";
    }
}
