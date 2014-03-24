package fi.uta.fsd.metka.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Configuration types enumerator.
 * Discriminator value uses the Values constants.
 */
public enum ConfigurationType {
    STUDY(Values.STUDY),
    SERIES(Values.SERIES),
    FILE(Values.FILE),
    PUBLICATION(Values.PUBLICATION);
    // Add more as needed

    private final String value;

    ConfigurationType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ConfigurationType fromValue(String value) {
        for(ConfigurationType type : values()) {
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

    public static class Values {
        public static final String STUDY = "STUDY";
        public static final String SERIES = "SERIES";
        public static final String FILE = "FILE";
        public static final String PUBLICATION = "PUBLICATION";
    }
}
