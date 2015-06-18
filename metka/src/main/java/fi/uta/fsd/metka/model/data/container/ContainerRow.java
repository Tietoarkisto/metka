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
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data_container_row.graphml
 */
public abstract class ContainerRow {

    private final String key;
    private final Integer rowId;
    private Boolean removed = false;
    private DateTimeUserPair saved;
    private Boolean unapproved = false;

    @JsonIgnore private RowContainerDataField rowContainer;

    @JsonCreator
    public ContainerRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        this.key = key;
        this.rowId = rowId;
    }

    public String getKey() {
        return key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public Boolean getRemoved() {
        return (removed == null ? false : removed);
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null ? false : removed);
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public Boolean getUnapproved() {
        return unapproved == null ? false : unapproved;
    }

    public void setUnapproved(Boolean unapproved) {
        this.unapproved = unapproved == null ? false : unapproved;
    }

    public RowContainerDataField getRowContainer() {
        return rowContainer;
    }

    public void setRowContainer(RowContainerDataField rowContainer) {
        this.rowContainer = rowContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContainerRow that = (ContainerRow) o;

        if (!key.equals(that.key)) return false;
        if (!rowId.equals(that.rowId)) return false;

        return true;
    }

    public StatusCode changeStatusFor(Language language, boolean newStatus, Map<String, Change> changeMap, DateTimeUserPair info) {
        if(changeMap == null || removed == newStatus) {
            return StatusCode.NO_CHANGE_IN_VALUE;
        }

        removed = newStatus;
        ContainerChange containerChange = (ContainerChange)changeMap.get(getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(getKey());
            changeMap.put(getKey(), containerChange);
        }
        if(containerChange.get(getRowId()) == null) {
            containerChange.put(new RowChange(getRowId()));
        }
        setSaved(info);
        return StatusCode.ROW_CHANGE;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + rowId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+getKey()+", rowId="+rowId+"]";
    }
}
