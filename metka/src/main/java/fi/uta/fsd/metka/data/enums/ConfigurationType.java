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
    PUBLICATION(Values.PUBLICATION),
    STUDY_ATTACHMENT(Values.STUDY_ATTACHMENT),
    STUDY_VARIABLES(Values.STUDY_VARIABLES),
    STUDY_VARIABLE(Values.STUDY_VARIABLE);
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

    public static boolean isValue(String value) {
        for(ConfigurationType type : values()) {
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
        public static final String STUDY = "STUDY";
        public static final String SERIES = "SERIES";
        public static final String PUBLICATION = "PUBLICATION";
        public static final String STUDY_ATTACHMENT = "STUDY_ATTACHMENT";
        public static final String STUDY_VARIABLES = "STUDY_VARIABLES";
        public static final String STUDY_VARIABLE = "STUDY_VARIABLE";
    }
}
