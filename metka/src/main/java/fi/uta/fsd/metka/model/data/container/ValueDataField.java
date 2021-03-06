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
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data_value_data_field.graphml
 */
public class ValueDataField extends DataField {
    /**
     * Builder method
     * @param key
     * @return
     */
    public static ValueDataField build(String key) {
        return new ValueDataField(key);
    }

    private final Map<Language, ValueContainer> original = new HashMap<>();
    private final Map<Language, ValueContainer> current = new HashMap<>();

    @JsonCreator
    public ValueDataField(@JsonProperty("key") String key) {
        // Let's just assume that polymorphism works and that the given type is a correct type
        super(DataFieldType.VALUE, key);
    }

    public Map<Language, ValueContainer> getOriginal() {
        return original;
    }

    public Map<Language, ValueContainer> getCurrent() {
        return current;
    }

    public ValueContainer getOriginalFor(Language language) {
        return original.get(language);
    }

    public void setOriginalFor(Language language, ValueContainer originalValue) {
        original.put(language, originalValue);
    }

    public ValueContainer getCurrentFor(Language language) {
        return current.get(language);
    }

    public void setCurrentFor(Language language, ValueContainer currentValue) {
        current.put(language, currentValue);
    }

    /**
     * Convenience method for getting an up to date value.
     * If there exists a modified value then return that, otherwise return original value.
     * @return SavedValue, either modified value if exists or original value. Can return null if both are null.
     */
    @JsonIgnore
    public ValueContainer getValueFor(Language language) {
        return (getCurrentFor(language) != null) ? current.get(language) : original.get(language);
    }

    /**
     * Convenience method for returning the actual value in this ValueDataField.
     * NOTICE: Returns empty string if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or empty string if value doesn't exist
     */
    @JsonIgnore
    public String getActualValueFor(Language language) {
        String value = "";
        if(hasValueFor(language)) {
            value = getValueFor(language).getActualValue();
        }
        return value;
    }

    @JsonIgnore public boolean hasOriginalFor(Language language) {
        return getOriginalFor(language) != null;
    }

    @JsonIgnore public boolean hasCurrentFor(Language language) {
        return getCurrentFor(language) != null;
    }

    /**
     * Convenience method to check if there exists a ValueContainer object for given language
     * and if that object actually has content.
     * @param language
     * @return
     */
    @JsonIgnore public boolean hasValueFor(Language language) {
        return getValueFor(language) != null && getValueFor(language).hasValue();
    }

    @JsonIgnore public boolean hasAnyValue() {
        for(Language l : Language.values()) {
            if(hasValueFor(l)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean originalForEquals(Language language, String compare) {
        return hasOriginalFor(language) && getOriginalFor(language).valueEquals(compare);
    }

    @JsonIgnore
    public boolean currentForEquals(Language language, String compare) {
        return hasCurrentFor(language) && getCurrentFor(language).valueEquals(compare);
    }

    /**
     * Checks if current and original values for given language are equal.
     * NOTICE: Unlike other equality checks this assumes that null values are infact equal
     * @param language
     * @return
     */
    @JsonIgnore
    public boolean currentForEqualsOriginal(Language language) {
        if(hasCurrentFor(language) != hasOriginalFor(language)) {
            return false;
        }
        if(!hasCurrentFor(language) && !hasOriginalFor(language)) {
            return true;
        } else {
            return getCurrentFor(language).valueEquals(getOriginalFor(language).getActualValue());
        }
    }

    /**
     * Convenience method for checking if the most recent value on this ValueDataField equals the given value.
     * NOTICE: Returns false if there is no value since null values should not be equal by default.
     *
     * @param compare - Value to compare
     * @return True if the value in this ValueDataField equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueForEquals(Language language, String compare) {
        return hasValueFor(language) && getValueFor(language).valueEquals(compare);
    }

    /**
     * Convenience method for checking if the most recent value on this ValueDataField equals the given value.
     * NOTICE: Returns false if there is no value since null values should not be equal by default.
     *
     * @param compare - Value to compare
     * @return True if the value in this ValueDataField equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueForIncludes(Language language, String compare) {
        return hasValueFor(language) && getValueFor(language).valueIncludes(compare);
    }

    @JsonIgnore
    public void initParents(DataFieldContainer parent) {
        setParent(parent);
    }

    @Override
    public DataField copy() {
        ValueDataField field = new ValueDataField(getKey());
        for(Language l : original.keySet()) {
            if(original.get(l) != null) {
                field.original.put(l, original.get(l).copy());
            }
        }
        for(Language l : current.keySet()) {
            if(current.get(l) != null) {
                field.current.put(l, current.get(l).copy());
            }
        }

        return field;
    }

    @Override
    public void normalize() {
        // If there's no modified value then don't do anything
        for(Language language : Language.values()) {
            if(current.get(language) != null) {
                original.put(language, current.get(language));
                current.remove(language);
            }
        }
    }
}
