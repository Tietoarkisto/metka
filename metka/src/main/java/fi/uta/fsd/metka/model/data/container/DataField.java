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

import com.fasterxml.jackson.annotation.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
/**
 * Specification and documentation is found from uml/data/uml_json_data_container_row.graphml
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueDataField.class, name = DataField.DataFieldType.Types.VALUE),
        @JsonSubTypes.Type(value = ContainerDataField.class, name = DataField.DataFieldType.Types.CONTAINER),
        @JsonSubTypes.Type(value = ReferenceContainerDataField.class, name = DataField.DataFieldType.Types.REFERENCECONTAINER)
})
public abstract class DataField {
    private final String key;

    @JsonIgnore private final DataFieldType type;

    @JsonIgnore private DataFieldContainer parent; // This is used during restriction validation

    public DataField(DataFieldType type, String key) {
        this.type = type;
        this.key = key;
    }

    public DataFieldType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataField that = (DataField) o;

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

    @JsonIgnore
    public abstract DataField copy();

    /**
     * Normalizes this data field for new revision.
     * Operations depend on the actual type of field.
     */
    @JsonIgnore
    public void normalize() {throw new UnsupportedOperationException();}

    public DataFieldContainer getParent() {
        return parent;
    }

    public void setParent(DataFieldContainer parent) {
        this.parent = parent;
    }

    // There's no null version since DataFields are always under some DataFieldContainer
    public abstract void initParents(DataFieldContainer parent);

    public static enum DataFieldType {
        VALUE(Types.VALUE),
        CONTAINER(Types.CONTAINER),
        REFERENCECONTAINER(Types.REFERENCECONTAINER);

        private String value;
        private DataFieldType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static DataFieldType fromValue(String value) {
            switch(value) {
                case Types.VALUE:
                    return VALUE;
                case Types.CONTAINER:
                    return CONTAINER;
                case Types.REFERENCECONTAINER:
                    return REFERENCECONTAINER;
            }
            throw new UnsupportedOperationException(value + " is not a valid DataFieldType");
        }

        public static class Types {
            public static final String VALUE = "VALUE";
            public static final String CONTAINER = "CONTAINER";
            public static final String REFERENCECONTAINER = "REFERENCECONTAINER";
        }
    }
}
