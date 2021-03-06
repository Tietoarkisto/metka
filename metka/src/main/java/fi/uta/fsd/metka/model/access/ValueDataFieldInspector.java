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

package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.ValueDataFieldAccessor.getValueDataField;

final class ValueDataFieldInspector {
    /**
     * Private constructor to disable instantiation.
     */
    private ValueDataFieldInspector() {}

    /**
     * Return what would happen if given value would be set to given DataField
     * map with given key.
     *
     * @param language      Language
     * @param fieldMap      map
     * @param key           key
     * @param value         value
     * @param config        config
     * @param configChecks  config checks
     * @return              status code and saved data field pair
     */
    static Pair<StatusCode, ValueDataField> checkValueDataFieldValue(Language language, Map<String, DataField> fieldMap, String key,
                                                                     Value value, Configuration config, ConfigCheck[] configChecks) {
        // Null maps or empty key results in incorrect parameters
        if(fieldMap == null || !StringUtils.hasText(key) || language == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Get status code and saved data field pair
        Pair<StatusCode, ValueDataField> pair = getValueDataField(fieldMap, key, config, configChecks);

        // Status code is never null but saved data field can be null
        StatusCode statusCode = pair.getLeft();

        // Field does not have configuration
        if (statusCode == StatusCode.CONFIG_FIELD_MISSING) {
            // Case where f.ex. it should not be written to revision data
            return pair;
        }

        // Check for translatability, this requires the precence of configuration
        if(config != null) {
            // We know that this field exists since otherwise getValueDataField would have returned CONFIG_FIELD_MISSING
            Field field = config.getField(key);
            if(!field.getTranslatable() && language != Language.DEFAULT) {
                return new ImmutablePair<>(StatusCode.FIELD_NOT_TRANSLATABLE, pair.getRight());
            }
        }

        // Field has configuration but does not exist
        if (statusCode == StatusCode.FIELD_MISSING) {
            // Config and field for key in config can be null

            // If value is null or empty no change
            if ( !value.hasValue() ) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            if (config != null  && config.getField(key) != null) {
                // Check field configuration
                Field field = config.getField(key);

                // If false value should not be written in revision data
                if ( !field.getWritable() ) {
                    // Field is not writable
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_WRITABLE
                            , null);
                }

                // If false user cannot edit value
                if ( !field.getEditable() ) {
                    // Field is not editable by user
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_EDITABLE
                            , null);
                }
                // No existing value so immutability does not matter
            }

            // Field is writable and editable and value is not null or empty
            // results in field insert. Saved data field is null in this case
            return new ImmutablePair<>(StatusCode.FIELD_INSERT, null);
        }

        // Field found
        if (statusCode == StatusCode.FIELD_FOUND) {
            // Config and field for key in config can be null but saved data
            // field is not null
            ValueDataField valueDataField = pair.getRight();

            // Old and new value null or empty results in no change
            if (!value.hasValue() && !valueDataField.hasValueFor(language)) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            // Old and new value equals results in no change
            if (valueDataField.valueForEquals(language, value.getValue())) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            if(valueDataField.hasValueFor(language) && value.isNull()) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            if (config != null  && config.getField(key) != null) {
                // Check field configuration
                Field field = config.getField(key);

                // If false value should not be written in revision data
                if(!field.getWritable() ) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_WRITABLE, null);
                }

                // If false user cannot edit value
                if(!field.getEditable()) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_EDITABLE, null);
                }

                // Check mutability
                if(field.getImmutable() && valueDataField.hasOriginalFor(language) && !valueDataField.originalForEquals(language, value.getValue())) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_MUTABLE, null);
                }
            }

            // Field is writable, mutable and editable results in field update
            return new ImmutablePair<>(StatusCode.FIELD_UPDATE, valueDataField);
        }

        // Catch all other status codes ?
        return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
    }

}
