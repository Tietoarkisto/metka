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

package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
/**
 * Specification and documentation is found from uml/data/uml_json_data_container_data_field.graphml
 */
public class ContainerDataField extends RowContainerDataField {
    /**
     * Contains rows for each language specified in Language enumerator.
     * Use of this must be careful
     */
    private final Map<Language, List<DataRow>> rows = new HashMap<>();

    @JsonCreator
    public ContainerDataField(@JsonProperty("key") String key, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(DataFieldType.CONTAINER, key, rowIdSeq);
    }

    public Map<Language, List<DataRow>> getRows() {
        return rows;
    }

    @JsonIgnore public List<DataRow> getRowsFor(Language language) {
        return rows.get(language);
    }

    @JsonIgnore public boolean hasRowsFor(Language language) {
        return rows.get(language) != null && !rows.get(language).isEmpty();
    }

    @JsonIgnore public boolean hasValidRowsFor(Language language) {
        if(rows.get(language) == null) {
            return false;
        }
        for(DataRow row : rows.get(language)) {
            if(!row.getRemoved()) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore public boolean hasRows() {
        for(Language language : Language.values()) {
            if(hasRowsFor(language)) return true;
        }
        return false;
    }

    @JsonIgnore public boolean hasValidRows() {
        for(Language language : Language.values()) {
            if(hasValidRowsFor(language)) return true;
        }
        return false;
    }

    /**
     * Inserts a row to given language if row with same id doesn't exist yet in any language
     * @param language
     * @param row
     */
    @JsonIgnore public void addRow(Language language, DataRow row) {
        if(getRowWithId(row.getRowId()).getLeft() == StatusCode.ROW_FOUND) {
            // Can't add second row with same id. Possibly should inform user but this should never happen anyway
            return;
        }
        if(rows.get(language) == null) {
            rows.put(language, new ArrayList<DataRow>());
        }
        rows.get(language).add(row);
    }

    /**
     * Creates a new row and inserts it to this ContainerDataField and adds a change to changeMap
     * @param language For which the row is inserted
     * @param changeMap Change map that should contain the ContainerChange for this container
     * @return
     */
    public Pair<StatusCode, DataRow> insertNewDataRow(Language language, Map<String, Change> changeMap) {
        if(language == null || changeMap == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        ContainerChange change = (ContainerChange)changeMap.get(getKey());
        if(change == null) {
            change = new ContainerChange(getKey());
            changeMap.put(change.getKey(), change);
        }

        DataRow row = DataRow.build(this);
        row.setUnapproved(true);
        change.put(new RowChange(row.getRowId()));
        change.setChangeIn(language);
        addRow(language, row);
        return new ImmutablePair<>(StatusCode.ROW_INSERT, row);
    }

    public Pair<StatusCode, DataRow> getRowWithIdFrom(Language language, Integer rowId) {
        if(!hasRowsFor(language)) {
            return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
        }
        for(DataRow row : getRowsFor(language)) {
            if(row.getRowId().equals(rowId)) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, row);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    /**
     * Searches all languages for a row.
     * Since rowId is unique across all languages in a certain container there is
     * no need to define a language when searching with rowId
     * @param rowId Row id to be searched for amongst rows
     * @return DataRow matching given value or null if none found
     */
    public Pair<StatusCode, DataRow> getRowWithId(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        for(Language language : Language.values()) {
            Pair<StatusCode, DataRow> rowPair = getRowWithIdFrom(language, rowId);
            if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                return rowPair;
            }
        }
        // Given rowId was not found from this container
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    public Pair<StatusCode, Language> getLanguageFor(Integer rowId) {
        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        for(Language language : Language.values()) {
            Pair<StatusCode, DataRow> rowPair = getRowWithIdFrom(language, rowId);
            if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                return new ImmutablePair<>(rowPair.getLeft(), language);
            }
        }
        // Given rowId was not found from this container
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    /**
     * Searches through a list of rows for a row where the value in a field with given id contains the given value.
     * Language needs to be defined since rows in different languages can have the same field value
     * and only the first according to Language.getValues() would ever be found.
     * NOTICE: null values are newer considered equal so you can't find rows with null field value
     * @param language The language for which the search is performed
     * @param key Field key of field where value should be found
     * @param value Value that is searched for
     * @return DataRow that contains given value in requested field, or null if not found.
     */
    public Pair<StatusCode, DataRow> getRowWithFieldIncludingValue(Language language, String key, Value value) {
        if(!hasRowsFor(language)) {
            return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
        }
        for(DataRow row : rows.get(language)) {
            if(row.getRemoved()) {
                continue;
            }
            Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(key));
            if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                continue;
            }
            ValueDataField field = pair.getRight();
            if(field.valueForIncludes(language, value.getValue())) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, row);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Searches through a list of rows for a row containing given value in a field with given id.
     * Language needs to be defined since rows in different languages can have the same field value
     * and only the first according to Language.getValues() would ever be found.
     * NOTICE: null values are newer considered equal so you can't find rows with null field value
     * @param language The language for which the search is performed
     * @param key Field key of field where value should be found
     * @param value Value that is searched for
     * @return DataRow that contains given value in requested field, or null if not found.
     */
    public Pair<StatusCode, DataRow> getRowWithFieldValue(Language language, String key, Value value) {
        if(!hasRowsFor(language)) {
            return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
        }
        for(DataRow row : rows.get(language)) {
            if(row.getRemoved()) {
                continue;
            }
            Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(key));
            if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                continue;
            }
            ValueDataField field = pair.getRight();
            if(field.valueForEquals(language, value.getValue())) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, row);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Uses getRowWithFieldValue to search for existing row with given value in a given field.
     * If row is not found creates a new row and inserts it to the list.
     * Since it can be assumed that it's desirable to find the field with the given value from the rows list
     * the field is created on the row with the given value
     * Language needs to be defined since rows in different languages can have the same field value
     * and only the first according to Language.getValues() would ever be found and we need to know to which
     * language to create the row.
     * @param language The language for which the search is performed and where row is created
     * @param key Field key of the field where the value should be found
     * @param value Value that is searched for
     * @param changeMap Map that should contain the change for the parent container of the new row
     * @param info DateTimeUserPair for possible creation of row and field. Can be null
     * @return Tuple of StatusCode and DataRow. StatusCode tells if the returned row is a new insert or not
     */
    public Pair<StatusCode, DataRow> getOrCreateRowWithFieldValue(Language language, String key, Value value, Map<String, Change> changeMap, DateTimeUserPair info) {
        if(changeMap == null || !value.hasValue()) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, DataRow> pair = getRowWithFieldValue(language, key, value);
        if(pair.getLeft() == StatusCode.ROW_FOUND) {
            return pair;
        } else {
            if(info == null) {
                info = DateTimeUserPair.build();
            }
            ContainerChange change = (ContainerChange)changeMap.get(getKey());
            if(change == null) {
                change = new ContainerChange(getKey());
                changeMap.put(getKey(), change);
            }

            DataRow row = DataRow.build(this);
            addRow(language, row);
            RowChange rowChange = new RowChange(row.getRowId());
            change.put(rowChange);

            row.dataField(ValueDataFieldCall.set(key, value, language).setInfo(info).setChangeMap(changeMap));
            return new ImmutablePair<>(StatusCode.ROW_INSERT, row);
        }
    }

    public Pair<StatusCode, DataRow> removeRow(Integer rowId, Map<String, Change> changeMap, DateTimeUserPair info) {
        Pair<StatusCode, DataRow> rowPair = getRowWithId(rowId);
        if(rowPair.getLeft() != StatusCode.ROW_FOUND) {
            return rowPair;
        }
        DataRow row = rowPair.getRight();
        // We know that the row exists so it has to have a language
        Language language = getLanguageFor(rowId).getRight();

        StatusCode status = row.changeStatusFor(language, true, changeMap, info);
        if(status == StatusCode.NO_CHANGE_IN_VALUE) {
            return new ImmutablePair<>(status, row);
        }
        // Row was removed, check that if it's an unapproved row then remove it completely. We don't need to keep removed unapproved rows.
        if(status == StatusCode.ROW_CHANGE && row.getUnapproved() && row.getRemoved()) {
            List<DataRow> rows = getRowsFor(language);
            for(Iterator<DataRow> i = rows.iterator(); i.hasNext();) {
                DataRow next = i.next();
                if(next.getRowId().equals(rowId)) {
                    i.remove();
                    status = StatusCode.ROW_REMOVED;
                    break;
                }
            }
            if(status == StatusCode.ROW_REMOVED) {
                ContainerChange cc = (ContainerChange)changeMap.get(getKey());
                // This check shouldn't really fail
                if(cc != null) {
                    if(cc.get(rowId) != null) {
                        cc.getRows().remove(rowId);
                    }
                }
            }
        }
        return new ImmutablePair<>(status, row);
    }

    @Override
    public Set<Integer> getRowIdsFor(Language language) {
        Set<Integer> ids = new HashSet<>();
        if(!hasRowsFor(language)) {
            return ids;
        }
        for(DataRow row : getRowsFor(language)) {
            ids.add(row.getRowId());
        }
        return ids;
    }

    @Override
    public void initParents(DataFieldContainer parent) {
        setParent(parent);
        for(Language l : rows.keySet()) {
            for(DataRow row : rows.get(l)) {
                row.setRowContainer(this);
                row.initParents(parent);
            }
        }
    }

    @Override
    public DataField copy() {
        ContainerDataField container = new ContainerDataField(getKey(), getRowIdSeq());
        //container.setType(getType());
        for(Language language : Language.values()) {
            if(!hasRowsFor(language)) {
                continue;
            }
            if(hasRowsFor(language)) {
                for(DataRow row : rows.get(language)) {
                    container.addRow(language, row.copy());
                }
            }
        }
        return container;
    }

    @Override
    public void normalize() {
        List<DataRow> remove = new ArrayList<>();
        // If row is removed mark it for removal, otherwise normalize row
        for(Language language : Language.values()) {
            if(!hasRowsFor(language)) {
                continue;
            }
            remove.clear();
            List<DataRow> languageRows = getRowsFor(language);
            for(DataRow row : languageRows) {
                if(row.getRemoved()) {
                    remove.add(row);
                } else {
                    row.normalize();
                }
            }

            // Remove all rows marked for removal
            for(DataRow row : remove) {
                languageRows.remove(row);
            }
        }
    }
}
