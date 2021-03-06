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
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Specification and documentation is found from uml/data/uml_json_data_reference_container_data_field.graphml
 */
public class ReferenceContainerDataField extends RowContainerDataField {
    private final List<ReferenceRow> references = new ArrayList<>();

    @JsonCreator
    public ReferenceContainerDataField(@JsonProperty("key") String key, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(DataFieldType.REFERENCECONTAINER, key, rowIdSeq);
    }

    public List<ReferenceRow> getReferences() {
        return references;
    }

    /**
     * Searches through a list of references for a reference with given rowId
     * @param rowId Row id to be searched for amongst references
     * @return ReferenceRow matching given value or null if none found
     */
    @JsonIgnore public Pair<StatusCode, ReferenceRow> getReferenceWithId(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        for(ReferenceRow reference : references) {
            if(reference.getRowId().equals(rowId)) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, reference);
            }
        }
        // Given rowId was not found from this container
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    /**
     * Searches through a list of references for a reference containing given value
     * @param value Reference value that is searched for
     * @return ReferenceRow matching given value or null if none found
     */
    @JsonIgnore public Pair<StatusCode, ReferenceRow> getReferenceWithValue(String value) {
        for(ReferenceRow reference : references) {
            if(reference.valueEquals(value) && !reference.getRemoved()) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, reference);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Searches through a list of references for a reference that contains given value in its own value.
     * This is typically used to find rows based on revisionable id in reference containers with REVISION type reference.
     * WARNING: This finds only the first instance, if all matching rows are needed then some other method has to be used.
     * @param value Reference value that is searched for
     * @return ReferenceRow matching given value or null if none found
     */
    @JsonIgnore public Pair<StatusCode, ReferenceRow> getReferenceIncludingValue(String value) {
        for(ReferenceRow reference : references) {
            if(reference.valueContaints(value) && !reference.getRemoved()) {
                return new ImmutablePair<>(StatusCode.ROW_FOUND, reference);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Uses getReferenceWithValue to search for existing reference with given value.
     * If reference is not found creates a new reference and inserts it to the list.
     * Since it can be assumed that it's desirable to find the reference with the given value from the references list
     * the reference is created with the given value
     *
     * @param value Value that is searched for
     * @param changeMap Map where the container change containing this reference containers changes should reside
     * @param info DateTimeUserPair if new reference is needed. If this is null then new instance is used.
     * @return Tuple of StatusCode and ReferenceRow. StatusCode tells if the returned row is a new insert or not
     */
    @JsonIgnore public Pair<StatusCode, ReferenceRow> getOrCreateReferenceWithValue(String value, Map<String, Change> changeMap, DateTimeUserPair info) {
        if(changeMap == null || !StringUtils.hasText(value)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        Pair<StatusCode, ReferenceRow> pair = getReferenceWithValue(value);
        if(pair.getLeft() == StatusCode.ROW_FOUND) {
            return pair;
        } else {
            if(info == null) {
                info = DateTimeUserPair.build();
            }
            ReferenceRow reference = ReferenceRow.build(this, new Value(value), info);
            reference.setUnapproved(true);
            references.add(reference);

            ContainerChange change = (ContainerChange)changeMap.get(reference.getKey());
            if(change == null) {
                change = new ContainerChange(reference.getKey());
                changeMap.put(change.getKey(), change);
            }
            change.put(new RowChange(reference.getRowId()));
            return new ImmutablePair<>(StatusCode.ROW_INSERT, reference);
        }
    }

    @JsonIgnore public Pair<StatusCode, ReferenceRow> removeReference(Integer rowId, Map<String, Change> changeMap, DateTimeUserPair info) {
        Pair<StatusCode, ReferenceRow> rowPair = getReferenceWithId(rowId);
        if(rowPair.getLeft() != StatusCode.ROW_FOUND) {
            return rowPair;
        }
        ReferenceRow row = rowPair.getRight();

        StatusCode status = row.changeStatusFor(Language.DEFAULT, true, changeMap, info);
        if(status == StatusCode.NO_CHANGE_IN_VALUE) {
            return new ImmutablePair<>(status, row);
        }
        // Row was removed, check that if it's an unapproved row then remove it completely. We don't need to keep removed unapproved rows.
        if(status == StatusCode.ROW_CHANGE && row.getUnapproved() && row.getRemoved()) {
            for(Iterator<ReferenceRow> i = references.iterator(); i.hasNext();) {
                ReferenceRow next = i.next();
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

    @JsonIgnore public boolean hasRows() {
        return !references.isEmpty();
    }

    @JsonIgnore public boolean hasValidRows() {
        for(ReferenceRow row : references) {
            if(!row.getRemoved()) {
                return true;
            }
        }
        return false;
    }

    @Override
    @JsonIgnore public Set<Integer> getRowIdsFor(Language language) {
        Set<Integer> ids = new HashSet<>();
        if(language == Language.DEFAULT) {
            for(ReferenceRow reference : references) {
                ids.add(reference.getRowId());
            }
        }
        return ids;
    }

    /**
     * Replaces the reference row with given rowId with the provided new reference row.
     * In actuality the new row is inserted just after the found row and the found row is set to removed=true
     * @param rowId
     * @param newRow
     * @param changeMap
     */
    @JsonIgnore
    public void replaceRow(Integer rowId, ReferenceRow newRow, Map<String, Change> changeMap) {
        ListIterator<ReferenceRow> i = references.listIterator();
        while(i.hasNext()) {
            ReferenceRow row = i.next();
            if(row.getRowId() == rowId) {
                i.set(newRow);
                break;
            }
        }
    }


    @Override
    public void initParents(DataFieldContainer parent) {
        setParent(parent);
        for(ReferenceRow row : references) {
            row.setRowContainer(this);
        }
    }

    @Override
    @JsonIgnore
    public DataField copy() {
        ReferenceContainerDataField container = new ReferenceContainerDataField(getKey(), getRowIdSeq());
        //container.setType(getType());
        for(ReferenceRow reference : references) {
            container.references.add(reference.copy());
        }
        return container;
    }

    @Override
    public void normalize() {
        List<ReferenceRow> remove = new ArrayList<>();
        for(ReferenceRow reference : references) {
            if(reference.getRemoved()) {
                remove.add(reference);
            } else {
                reference.normalize();
            }
        }
        for(ReferenceRow reference : remove) {
            references.remove(reference);
        }
    }
}
