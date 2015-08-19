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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.SelectionListType;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_selection_list.graphml
 */
@JsonIgnoreProperties("_comment")
public class SelectionList {
    private final String key;
    @JsonProperty("default") private String def = null;
    private final List<Option> options = new ArrayList<>();
    private SelectionListType type;
    private String reference;
    private Boolean includeEmpty = true;
    private final List<String> freeText = new ArrayList<>();
    private String freeTextKey;
    private String sublistKey;

    @JsonCreator
    public SelectionList(@JsonProperty("key") String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public List<Option> getOptions() {
        return options;
    }

    public SelectionListType getType() {
        return type;
    }

    public void setType(SelectionListType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getIncludeEmpty() {
        return (includeEmpty == null) ? false : includeEmpty;
    }

    public void setIncludeEmpty(Boolean includeEmpty) {
        this.includeEmpty = (includeEmpty == null) ? false : includeEmpty;
    }

    public List<String> getFreeText() {
        return freeText;
    }

    public String getFreeTextKey() {
        return freeTextKey;
    }

    public void setFreeTextKey(String freeTextKey) {
        this.freeTextKey = freeTextKey;
    }

    public String getSublistKey() {
        return sublistKey;
    }

    public void setSublistKey(String sublistKey) {
        this.sublistKey = sublistKey;
    }

    /**
     * Helper method to return option with given value.
     * Only works with LITERAL and VALUE lists.
     * Returns null if option not found or type is wrong.
     * @param value
     * @return
     */
    public Option getOptionWithValue(String value) {
        if(type == SelectionListType.SUBLIST || type == SelectionListType.REFERENCE) {
            return null;
        }
        for(Option option : options) {
            if(option.getValue().equals(value)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectionList that = (SelectionList) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        String out = "selectionList["+key+"], options[";
        for(Option option : options) {
            out += option;
            out += ", ";
        }
        out += "]";
        return out;
    }
}
