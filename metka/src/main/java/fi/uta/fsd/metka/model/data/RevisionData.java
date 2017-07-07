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

package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.DataFieldOperator;
import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.calls.DataFieldCallBase;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data.graphml
 */
public class RevisionData implements Comparable<RevisionData>, ModelBase, DataFieldContainer {

    // Class
    private final RevisionKey key;
    private final ConfigurationKey configuration;
    private final Map<String, Change> changes = new HashMap<>();
    private final Map<String, DataField> fields = new HashMap<>();
    private RevisionState state;
    private String handler;
    private final Map<Language, ApproveInfo> approved = new HashMap<>();
    private DateTimeUserPair saved;

    @JsonIgnore private DataFieldContainer parent; // This is used for restrictions when validation jumps through reference barrier.

    @JsonCreator
    public RevisionData(@JsonProperty("key")RevisionKey key, @JsonProperty("configuration")ConfigurationKey configuration) {
        this.key = key;
        this.configuration = configuration;
    }

    public RevisionKey getKey() {
        return key;
    }

    @Override
    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    public Map<String, DataField> getFields() {
        return fields;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public Map<Language, ApproveInfo> getApproved() {
        return approved;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public String getHandler() {
        return (handler == null) ? "" : handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public DataFieldContainer getParent() {
        return parent;
    }

    @Override
    public void setParent(DataFieldContainer parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionData that = (RevisionData) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    // **************
    // Helper methods
    // **************

    public Change getChange(String key) {
        return changes.get(key);
    }
    public Change getChange(Field field) {
        return getChange(field.getKey());
    }

    @JsonIgnore public ApproveInfo approveInfoFor(Language language) {
        return approved.get(language);
    }
    @JsonIgnore public boolean isApprovedFor(Language language) {
        return approveInfoFor(language) != null && approveInfoFor(language).getApproved() != null && approveInfoFor(language).getApproved().getTime() != null;
    }

    @JsonIgnore public void approveRevision(Language language, ApproveInfo info) {
        approved.put(language, info);
    }

    @JsonIgnore public void initParents(DataFieldContainer parent) {
        setParent(parent);
        for(DataField field : fields.values()) {
            field.initParents(this);
        }
    }

    @JsonIgnore public void initParents() {
        initParents(null);
    }

    public RevisionData putChange(Change change) {
        if(change.getKey() != null) {
            changes.put(change.getKey(), change);
        }
        return this;
    }

    @Override
    @JsonIgnore public RevisionKey getRevision() {
        return getKey();
    }

    // *************************
    // Interface implementations
    // *************************

    // Comparable
    @Override
    public int compareTo(RevisionData o) {
        return key.compareTo(o.key);
    }

    // DataFieldContainer


    @Override
    @JsonIgnore public RevisionData getContainingRevision() {
        return this;
    }

    @Override
    @JsonIgnore public boolean hasFields() {
        return !fields.isEmpty();
    }

    @Override
    @JsonIgnore public DataField getField(String key) {
        return fields.get(key);
    }

    /**
     * Executes DataField operations on this RevisionData based on the DataFieldCall given.
     * If SET operation is requested and no ChangeMap is provided in call then this RevisionData's change map is provided by default
     * @param call
     * @param <T>
     * @return
     */
    @Override
    public <T extends DataField> Pair<StatusCode, T> dataField(DataFieldCall<T> call) {
        switch(call.getCallType()) {
            case GET:
                return DataFieldOperator.getDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            case CHECK:
                return DataFieldOperator.checkDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            case SET:
                if(call.getChangeMap() == null) ((DataFieldCallBase<T>)call).setChangeMap(getChanges());
                return DataFieldOperator.setDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }

    public static RevisionData buildFromTransferData(TransferData data){
        RevisionData revisionData = new RevisionData(new RevisionKey(data.getKey().getId(), data.getKey().getNo()), new ConfigurationKey(data.getConfiguration().getType(), data.getConfiguration().getVersion()));
        // Fields
        for (TransferField field : data.getFields().values()){
            switch (field.getType()){
                case VALUE:
                    ValueDataField newValueField = new ValueDataField(field.getKey());
                    for (Map.Entry<Language, TransferValue> value : field.getValues().entrySet()){
                        if (value.getValue().getOriginal() != null) {
                            newValueField.setOriginalFor(value.getKey(), new ValueContainer(null, value.getValue().originalAsValue()));
                        }
                        if (value.getValue().getCurrent() != null) {
                            newValueField.setCurrentFor(value.getKey(), new ValueContainer(null, value.getValue().currentAsValue()));
                        }
                    }
                    revisionData.getFields().put(field.getKey(), newValueField);
                    break;
                case CONTAINER:
                    ContainerDataField newContainerField = new ContainerDataField(field.getKey(), 0);
                    for (Map.Entry<Language, List<TransferRow>> langRows : field.getRows().entrySet()) {
                        List<DataRow> newLangContainerRows = new ArrayList<>();
                        for (TransferRow row : langRows.getValue()){
                            if (row.getRemoved())
                                continue;
                            DataRow newContainerRow = new DataRow(row.getKey(),newContainerField.getNewRowId());
                            for (Map.Entry<String, TransferField> rowField : row.getFields().entrySet()){
                                ValueDataField rowValueField = new ValueDataField(rowField.getKey());
                                for (Map.Entry<Language, TransferValue> rowValue : rowField.getValue().getValues().entrySet()){
                                    if (rowValueField.getOriginalFor(rowValue.getKey()) != null) {
                                        rowValueField.setOriginalFor(rowValue.getKey(), new ValueContainer(null, rowValue.getValue().originalAsValue()));
                                    }
                                    if (rowValueField.getCurrentFor(rowValue.getKey()) != null) {
                                        rowValueField.setCurrentFor(rowValue.getKey(), new ValueContainer(null, rowValue.getValue().currentAsValue()));
                                    }
                                }
                                newContainerRow.getFields().put(rowValueField.getKey(), rowValueField);
                            }
                            newLangContainerRows.add(newContainerRow);
                        }
                        newContainerField.getRows().put(langRows.getKey(), newLangContainerRows);
                    }
                    revisionData.getFields().put(newContainerField.getKey(), newContainerField);
                    break;
                case REFERENCECONTAINER:
                    ReferenceContainerDataField newReferenceContainerField = new ReferenceContainerDataField(field.getKey(), 0);
                    for (List<TransferRow> rows : field.getRows().values()){
                        for (TransferRow row : rows){
                            ReferenceRow referenceRow = new ReferenceRow(row.getKey(), newReferenceContainerField.getNewRowId(), new Value(row.getValue()));
                            newReferenceContainerField.getReferences().add(referenceRow);
                        }
                    }
                    revisionData.getFields().put(newReferenceContainerField.getKey(), newReferenceContainerField);
                    break;
            }
        }
        return revisionData;
    }
}
