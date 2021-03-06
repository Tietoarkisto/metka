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
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;


/**
 * Specification and documentation is found from uml/data/uml_json_data_reference_container_data_field.graphml
 */
public class ReferenceRow extends ContainerRow {
    public static ReferenceRow build(ReferenceContainerDataField container, Value reference, DateTimeUserPair info) {
        ReferenceRow row = new ReferenceRow(container.getKey(), container.getNewRowId(), reference);
        row.setSaved(info);
        return row;
    }

    public static ReferenceRow build(ReferenceContainerDataField container, Value reference, Integer rowId, DateTimeUserPair info) {
        ReferenceRow row = new ReferenceRow(container.getKey(), rowId, reference);
        row.setSaved(info);
        return row;
    }

    private final Value reference;

    @JsonCreator
    public ReferenceRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId, @JsonProperty("reference") Value reference) {
        super(key, rowId);
        this.reference = reference;
    }

    public Value getReference() {
        return reference;
    }

    /**
     * Convenience method for checking if this reference has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(reference == null) {
            return false;
        } else return reference.hasValue();
    }

    /**
     * Convenience method for checking if the reference equals the given value.
     * NOTICE: Returns false if there is no value.
     *
     * @param compare - Value to compare
     * @return True if the value in this reference equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueEquals(String compare) {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return reference.getValue().equals(compare);
        } else return false;
    }

    /**
     * Convenience method for returning the actual value in this ReferenceRow.
     * NOTICE: Returns null if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or null if value doesn't exist
     */
    @JsonIgnore
    public String getActualValue() {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return reference.getValue();
        } else return null;
    }

    @JsonIgnore
    public ReferenceRow copy() {
        ReferenceRow row = new ReferenceRow(getKey(), getRowId(), getReference());
        row.setSaved(getSaved());
        row.setRemoved(getRemoved());
        return row;
    }

    public StatusCode restore(Map<String, Change> changeMap, DateTimeUserPair info) {
        return super.changeStatusFor(Language.DEFAULT, false, changeMap, info);
    }

    public void normalize() {
        // There's nothing to normalize since changing a referenced value of a row doesn't really make sense
        // There either is a value or not and users have to remove a row and create new row to make 'change'
    }

    @JsonIgnore
    public boolean valueContaints(String value) {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return reference.getValue().contains(value);
        } else return false;
    }
}
