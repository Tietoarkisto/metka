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
import fi.uta.fsd.metka.model.data.container.ContainerRow;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Specification and documentation is found from uml/uml_json_transfer.graphml
 */
public class TransferRow implements TransferFieldContainer {
    private final String key;
    private Integer rowId;
    private String value = "";
    private DateTimeUserPair saved;
    private final Map<String, TransferField> fields = new HashMap<>();
    private final List<FieldError> errors = new ArrayList<>();
    private Boolean removed;
    private Boolean unapproved;

    @JsonCreator
    public TransferRow(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public Map<String, TransferField> getFields() {
        return fields;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public Boolean getRemoved() {
        return (removed == null ? false : removed);
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null ? false : removed);
    }

    public Boolean getUnapproved() {
        return unapproved;
    }

    public void setUnapproved(Boolean unapproved) {
        this.unapproved = unapproved;
    }

    @JsonIgnore
    public boolean hasField(String key) {
        return fields.containsKey(key) && fields.get(key) != null;
    }

    @JsonIgnore
    public TransferField getField(String key) {
        return fields.get(key);
    }

    @JsonIgnore
    public void addField(TransferField field) {
        if(hasField(field.getKey())) {
            return;
        }
        fields.put(field.getKey(), field);
    }

    public void addError(FieldError error) {
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

    public static TransferRow buildFromContainerRow(ContainerRow row) {
        // Add common info
        TransferRow transferRow = new TransferRow(row.getKey());
        transferRow.setRowId(row.getRowId());
        transferRow.setSaved(row.getSaved());
        transferRow.setRemoved(row.getRemoved());
        transferRow.setUnapproved(row.getUnapproved());

        if(row instanceof DataRow) {
            return buildRowFromDataRow(transferRow, (DataRow)row);
        } else if(row instanceof ReferenceRow) {
            return buildRowFromReferenceRow(transferRow, (ReferenceRow)row);
        }
        return null;
    }

    private static TransferRow buildRowFromDataRow(TransferRow transferRow, DataRow row) {
        for(DataField field : row.getFields().values()) {
            TransferField transferField = TransferField.buildFromDataField(field);
            if(transferField != null) {
                transferRow.fields.put(transferField.getKey(), transferField);
            }
        }
        return transferRow;
    }

    private static TransferRow buildRowFromReferenceRow(TransferRow transferRow, ReferenceRow row) {
        if(row.hasValue()) {
            transferRow.setValue(row.getActualValue());
        }
        return transferRow;
    }
}
