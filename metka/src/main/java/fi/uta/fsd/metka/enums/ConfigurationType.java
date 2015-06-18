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
