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
