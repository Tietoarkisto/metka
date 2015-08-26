/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

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
    public static final Value NULL = new Value(null);

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

    @JsonIgnore public boolean valueIncludes(String compare) {
        return hasValue() && value.contains(compare);
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
