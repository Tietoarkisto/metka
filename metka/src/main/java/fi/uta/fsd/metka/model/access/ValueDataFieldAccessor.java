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

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.DataFieldOperationChecks.*;
import static fi.uta.fsd.metka.model.access.DataFieldAccessor.getDataField;

final class ValueDataFieldAccessor {
    // Private constructor to disable instantiation
    private ValueDataFieldAccessor() {}

    /**
     * Returns requested field from given map as a ValueDataField.
     * If provided with configuration checks the field type to make sure a ValueDataField can be returned.
     * If field exists checks the instanceof value to make sure that the field can be returned as a ValueDataField.
     * This instanceof check is required since it's possible to modify the json-file by hand or batch processing to insert
     * fields that don't conform to configuration.
     * Priority is given to configuration so that if configuration tells us that the field can not be ValueDataField
     * then CONFIG_FIELD_TYPE_MISMATCH status is returned with null value right away even if there is a ValueDataField field present.
     *
     * @param fieldMap Map from where the field should be returned
     * @param key Field key of the requested field
     * @param config Configuration containing the requested field key. Can be null.
     * @return Tuple of StatusCode and ValueDataField. If status is FIELD_FOUND then ValueDataField is the requested field, otherwise ValueDataField is null
     */
    static Pair<StatusCode, ValueDataField> getValueDataField(Map<String, DataField> fieldMap, String key, Configuration config,
                                                              ConfigCheck[] configChecks) {
        if(fieldMap == null || !StringUtils.hasText(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, DataField> pair = getDataField(
                fieldMap,
                key,
                config,
                ArrayUtils.add(configChecks, ConfigCheck.NOT_CONTAINER),
                new FieldCheck[]{FieldCheck.VALUE_DATA_FIELD});
        return new ImmutablePair<>(pair.getLeft(), (ValueDataField)pair.getRight());
    }
}
