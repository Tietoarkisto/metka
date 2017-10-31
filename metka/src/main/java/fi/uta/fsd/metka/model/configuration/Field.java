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

package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldType;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_field.graphml
 */
@JsonIgnoreProperties("_comment")
public class Field {
    private final String key;
    private FieldType type;
    private Boolean translatable = true;
    private Boolean immutable = false;
    private Integer maxValues = null;
    private String selectionList = null;
    private Boolean subfield = false;
    private final List<String> subfields = new ArrayList<>();
    private String reference = null;
    private Boolean editable = true;
    private Boolean writable = true;
    private Boolean indexed = true;
    private Boolean generalSearch = false;
    private Boolean exact = true;
    private String bidirectional = null;
    private String indexName = null;
    private Boolean fixedOrder = true;

    private final Set<String> removePermissions = new HashSet<>();

    @JsonCreator
    public Field(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Boolean getTranslatable() {
        return translatable;
    }

    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
    }

    public Boolean getImmutable() {
        return immutable;
    }

    public void setImmutable(Boolean immutable) {
        this.immutable = immutable;
    }

    public Integer getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(Integer maxValues) {
        this.maxValues = maxValues;
    }

    public String getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(String selectionList) {
        this.selectionList = selectionList;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Boolean getSubfield() {
        return (subfield == null) ? false : subfield;
    }

    public void setSubfield(Boolean subfield) {
        this.subfield = (subfield == null) ? false : subfield;
    }

    public List<String> getSubfields() {
        return subfields;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Boolean getIndexed() {
        return (indexed == null) ? true : indexed;
    }

    public void setIndexed(Boolean indexed) {
        this.indexed = (indexed == null) ? true : indexed;
    }

    public Boolean getGeneralSearch() {
        return generalSearch == null ? false : generalSearch;
    }

    public void setGeneralSearch(Boolean generalSearch) {
        this.generalSearch = generalSearch == null ? false : generalSearch;
    }

    public Boolean getExact() {
        return (exact == null) ? true : exact;
    }

    public void setExact(Boolean exact) {
        this.exact = (exact == null) ? true : exact;
    }

    public String getBidirectional() {
        return bidirectional;
    }

    public void setBidirectional(String bidirectional) {
        this.bidirectional = bidirectional;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Boolean getFixedOrder() {
        return fixedOrder == null ? true : fixedOrder;
    }

    public void setFixedOrder(Boolean fixedOrder) {
        this.fixedOrder = fixedOrder == null ? true : fixedOrder;
    }

    public Set<String> getRemovePermissions() {
        return removePermissions;
    }

    @JsonIgnore public String getIndexAs() {
        return StringUtils.hasText(indexName) ? indexName : key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        return Objects.equals(key, field.key) && Objects.equals(this.getSelectionList(), field.getSelectionList()) && Objects.equals(this.getReference(), field.getReference()) && Objects.equals(this.getType(), field.getType())
                && Objects.equals(this.getIndexAs(), field.getIndexAs()) && Objects.equals(this.getEditable(), field.getEditable()) && Objects.equals(this.getTranslatable(), field.getTranslatable())
                && Objects.equals(this.getImmutable(), field.getImmutable()) && Objects.equals(this.getGeneralSearch(), field.getGeneralSearch()) && Objects.equals(this.getExact(), field.getExact())
                && Objects.equals(this.getBidirectional(), field.getBidirectional()) && Objects.equals(this.getFixedOrder(), field.getFixedOrder()) && Objects.equals(this.getSubfields(), field.getSubfields())
                && Objects.equals(this.getIndexName(), field.getIndexName()) && Objects.equals(this.getMaxValues(), field.getMaxValues()) && Objects.equals(this.getRemovePermissions(), field.getRemovePermissions())
                && Objects.equals(this.getSubfield(), field.getSubfield()) && Objects.equals(this.getWritable(), field.getWritable()) && Objects.equals(this.getIndexed(), field.getIndexed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, selectionList, reference, type, indexName, editable,
                translatable, immutable, generalSearch, exact, bidirectional,
                fixedOrder, subfields, indexed, maxValues, removePermissions, subfield, writable);
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+", type="+type.getValue()+"]";
    }
}
