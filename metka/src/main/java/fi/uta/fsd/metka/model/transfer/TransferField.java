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

package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.TransferFieldType;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Specification and documentation is found from uml/uml_json_transfer.graphml
 */
public class TransferField {
    private final String key;
    private final TransferFieldType type;
    private final Map<Language, TransferValue> values = new HashMap<>();
    private final Map<Language, List<TransferRow>> rows = new HashMap<>();
    private final List<FieldError> errors = new ArrayList<>();

    @JsonCreator
    public TransferField(@JsonProperty("key")String key, @JsonProperty("type")TransferFieldType type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public TransferFieldType getType() {
        return type;
    }

    public Map<Language, TransferValue> getValues() {
        return values;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    /**
     * Puts given value to values map with given language and returns the previous value.
     * @param language    Language
     * @param value       TransferValue
     * @return  TransferValue
     */
    @JsonIgnore public TransferValue addValueFor(Language language, TransferValue value) {
        TransferValue previous = values.get(language);
        values.put(language, value);
        return previous;
    }

    @JsonIgnore public TransferValue getValueFor(Language language) {
        return values.get(language);
    }

    @JsonIgnore public boolean containsValueFor(Language language) {
        return getValueFor(language) != null;
    }

    @JsonIgnore public boolean hasOriginalFor(Language language) {
        return containsValueFor(language) && getValueFor(language).hasOriginal();
    }

    @JsonIgnore public boolean hasCurrentFor(Language language) {
        return containsValueFor(language) && getValueFor(language).hasCurrent();
    }

    @JsonIgnore public boolean hasValueFor(Language language) {
        return containsValueFor(language) && getValueFor(language).hasValue();
    }

    @JsonIgnore public Value currentAsValueFor(Language language) {
        return (containsValueFor(language) ? getValueFor(language).currentAsValue() : Value.NULL);
    }

    @JsonIgnore public Value originalAsValueFor(Language language) {
        return (containsValueFor(language) ? getValueFor(language).originalAsValue() : Value.NULL);
    }

    @JsonIgnore public Value asValueFor(Language language) {
        return (containsValueFor(language) ? getValueFor(language).asValue() : Value.NULL);
    }

    public Map<Language, List<TransferRow>> getRows() {
        return rows;
    }

    @JsonIgnore public List<TransferRow> getRowsFor(Language language) {
        return rows.get(language);
    }

    @JsonIgnore public boolean hasRows() {
        for(Language language : Language.values()) {
            if(hasRowsFor(language)) return true;
        }
        return false;
    }

    @JsonIgnore public boolean hasRowsFor(Language language) {
        return rows.get(language) != null && !rows.get(language).isEmpty();
    }

    @JsonIgnore public void addError(FieldError error) {
        boolean found = false;
        for(FieldError e : errors) {
            if(e == error) {
                found = true;
                break;
            }
        }

        if(!found) {
            errors.add(error);
        }
    }

    /**
     * Adds error for value under given language, if value is missing a new empty value is created
     * @param language     Language
     * @param error        FieldError
     */
    @JsonIgnore public void addErrorFor(Language language, FieldError error) {
        TransferValue value = getValueFor(language);
        if(value == null) {
            value = new TransferValue();
            addValueFor(language, value);
        }
        value.addError(error);
    }

    @JsonIgnore
    public TransferRow getRow(Integer rowId) {

        for(Language language : Language.values()) {
            for(TransferRow row : rows.get(language)) {
                if(row.getRowId().equals(rowId)) {
                    return row;
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public void addRowFor(Language language, TransferRow row) {
        if(rows.get(language) == null) {
            rows.put(language, new ArrayList<TransferRow>());
        }
        rows.get(language).add(row);
    }

    @JsonIgnore
    public void addRowFor(Language language, TransferRow row, int index) {
        if(rows.get(language) == null) {
            rows.put(language, new ArrayList<TransferRow>());
        }
        if(rows.get(language).size() > index) {
            addRowFor(language, row);
        } else {
            rows.get(language).add(index, row);
        }
    }

    public static TransferField buildFromDataField(DataField field) {
        if(field instanceof ValueDataField) {
            return buildValueFieldFromValueDataField((ValueDataField) field);
        } else {
            return buildContainerFromDataField(field);
        }
    }

    private static TransferField buildValueFieldFromValueDataField(ValueDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.VALUE);
        for(Language language : Language.values()) {
            TransferValue value = TransferValue.buildFromValueDataFieldFor(language, field);
            if(value != null) transferField.values.put(language, value);
        }
        return transferField;
    }

    private static TransferField buildContainerFromDataField(DataField field) {
        if(field instanceof ContainerDataField) {
            return buildContainerFromContainerDataField((ContainerDataField)field);
        } else if(field instanceof ReferenceContainerDataField) {
            return buildContainerFromReferenceContainerDataField((ReferenceContainerDataField)field);
        }
        return null;
    }

    private static TransferField buildContainerFromContainerDataField(ContainerDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.CONTAINER);

        for(Language language : Language.values()) {
            if(!field.hasRowsFor(language)) {
                continue;
            }
            for(DataRow row : field.getRowsFor(language)) {
                TransferRow transferRow = TransferRow.buildFromContainerRow(row);
                if(transferRow != null) {
                    transferField.addRowFor(language, transferRow);
                }
            }
        }
        return transferField;
    }

    private static TransferField buildContainerFromReferenceContainerDataField(ReferenceContainerDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.REFERENCECONTAINER);
        transferField.rows.put(Language.DEFAULT, new ArrayList<TransferRow>());
        for(ReferenceRow reference : field.getReferences()) {
            TransferRow transferRow = TransferRow.buildFromContainerRow(reference);
            if(transferRow != null) {
                transferField.rows.get(Language.DEFAULT).add(transferRow);
            }
        }
        return transferField;
    }
}
