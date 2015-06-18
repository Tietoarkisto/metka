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

import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class DataFieldOperator {
    // Disable instantiation
    private DataFieldOperator() {}

    public static <T extends DataField> Pair<StatusCode, T> getDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call) {
        return getDataFieldOperation(fieldMap, call, null);
    }

    @SuppressWarnings("unchecked") // We can suppress unchecked cast warnings since field type is set by the DataFieldCall constructor and always matches up with the generic type
    public static <T extends DataField> Pair<StatusCode, T> getDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call, ConfigCheck[] configChecks) {
        if(fieldMap == null || call == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        Pair<StatusCode, T> field;
        switch(call.getFieldType()) {
            case VALUE_DATA_FIELD:
                /*Pair<StatusCode, ValueDataField> saved = ValueDataFieldAccessor.getValueDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);*/
                Pair<StatusCode, ValueDataField> value = ValueDataFieldAccessor.getValueDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(value.getLeft(), (T)value.getRight());
            case CONTAINER_DATA_FIELD:
                Pair<StatusCode, ContainerDataField> container = ContainerDataFieldAccessor.getContainerDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(container.getLeft(), (T)container.getRight());
            case REFERENCE_CONTAINER_DATA_FIELD:
                Pair<StatusCode, ReferenceContainerDataField> reference = ReferenceContainerDataFieldAccessor.getReferenceContainerDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(reference.getLeft(), (T)reference.getRight());
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }

    // We can suppress unchecked cast warnings since field type is set by the
    // DataFieldCall constructor and always matches up with the generic type
    @SuppressWarnings("unchecked")
    public static <T extends DataField> Pair<StatusCode, T> checkDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call, ConfigCheck[] configChecks) {
        if(fieldMap == null || call == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        switch(call.getFieldType()) {
            case VALUE_DATA_FIELD:
                Pair<StatusCode, ValueDataField> pair = ValueDataFieldInspector
                        .checkValueDataFieldValue(
                                call.getLanguage(),
                                fieldMap,
                                call.getKey(),
                                call.getValue(),
                                call.getConfiguration(),
                                configChecks);
                return new ImmutablePair<>(pair.getLeft(), (T)pair.getRight());
            case CONTAINER_DATA_FIELD:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
            case REFERENCE_CONTAINER_DATA_FIELD:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }

    public static <T extends DataField> Pair<StatusCode, T> setDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call) {
        return setDataFieldOperation(fieldMap, call, null);
    }

    @SuppressWarnings("unchecked") // We can suppress unchecked cast warnings since field type is set by the DataFieldCall constructor and always matches up with the generic type
    public static <T extends DataField> Pair<StatusCode, T> setDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call, ConfigCheck[] configChecks) {
        if(fieldMap == null || call == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        switch(call.getFieldType()) {
            case VALUE_DATA_FIELD:
                Pair<StatusCode, ValueDataField> saved = ValueDataFieldMutator
                        .setValueDataField(
                                call.getLanguage(), fieldMap, call.getKey(), call.getValue(), call.getInfo(), call.getChangeMap(),
                                call.getConfiguration(), configChecks);
                return new ImmutablePair<>(saved.getLeft(), (T)saved.getRight());
            case CONTAINER_DATA_FIELD:
                Pair<StatusCode, ContainerDataField> container = ContainerDataFieldMutator
                        .setContainerDataField(
                                fieldMap, call.getKey(), call.getChangeMap(),
                                call.getConfiguration(), configChecks);
                return new ImmutablePair<>(container.getLeft(), (T)container.getRight());
            case REFERENCE_CONTAINER_DATA_FIELD:
                Pair<StatusCode, ReferenceContainerDataField> reference = ReferenceContainerDataFieldMutator
                        .setReferenceContainerDataField(
                                fieldMap, call.getKey(), call.getChangeMap(),
                                call.getConfiguration(), configChecks);
                return new ImmutablePair<>(reference.getLeft(), (T)reference.getRight());
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }
}
