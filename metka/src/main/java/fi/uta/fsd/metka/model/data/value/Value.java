package fi.uta.fsd.metka.model.data.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.FieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.util.StringUtils;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.stringToLong;

/**
 * This contains only immutable value string and functions for checking its content and equality.
 * This is somewhat unnecessary and mostly here to strongly type value.
 * With this there's still the possibility to subclass value so that we have actual json numbers and booleans in the data instead of just strings.
 * Specification and documentation is found from uml/data/uml_json_data_value_data_field.graphml
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

    @JsonIgnore public Long asInteger() {
        return stringToLong(value);
    }

    @JsonIgnore public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

    @JsonIgnore public boolean isNull() {return value == null;}

    @JsonIgnore public boolean isEmpty() {return !isNull() && !StringUtils.hasText(value);}

    public Value copy() {
        return new Value(value);
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", value="+value+"]";
    }

    public FieldError typeCheck(FieldType type) {
        if(!StringUtils.hasText(value)) {
            return null;
        }
        switch(type) {
            case BOOLEAN:
                if(!value.equals("true") && !value.equals("false")) {
                    return FieldError.NOT_BOOLEAN;
                }
                break;
            case DATE:
                /*try {
                    new LocalDate(value);
                } catch (IllegalArgumentException iae) {
                    return FieldError.NOT_DATE;
                }
                break;*/
            case DATETIME:
                try {
                    new LocalDateTime(value);
                } catch (IllegalArgumentException iae) {
                    return FieldError.NOT_DATETIME;
                }
                break;
            case TIME:
                try {
                    new LocalTime(value);
                } catch (IllegalArgumentException iae) {
                    return FieldError.NOT_TIME;
                }
                break;
            case INTEGER:
                    try {
                        Long.parseLong(value);
                    } catch(NumberFormatException nfe) {
                        return FieldError.NOT_INTEGER;
                    }
                break;
            case REAL:
                    try {
                        Double.parseDouble(value);
                    } catch(NumberFormatException nfe) {
                        return FieldError.NOT_REAL;
                    }
                break;
        }
        return null;
    }
}
