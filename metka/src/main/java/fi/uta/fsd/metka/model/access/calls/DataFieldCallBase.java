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

package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;

public abstract class DataFieldCallBase<T extends DataField> implements DataFieldCall<T> {
    private final CallType callType;
    private final String key;
    private Value value;
    private Configuration configuration;
    private Map<String, Change> changeMap;
    private ContainerChange containerChange;
    private DateTimeUserPair info;
    private final DataFieldType fieldType;
    private Language language;

    private boolean isValueSet = false;

    protected DataFieldCallBase(DataFieldType fieldType, String key, CallType callType) {
        this.fieldType = fieldType;
        this.key = key;
        this.callType = callType;
    }

    // Getters
    public CallType getCallType() {return callType;}
    public String getKey() {return key;}
    public Value getValue() {return value;}
    public Configuration getConfiguration() {return configuration;}
    public Map<String, Change> getChangeMap() {return changeMap;}
    public DateTimeUserPair getInfo() {return info;}
    public DataFieldType getFieldType() {return fieldType;}
    public Language getLanguage() {return language;}
    public ContainerChange getContainerChange() {
        return containerChange;
    }

    // Setters
    public DataFieldCallBase<T> setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Shortcut to set the value with String.
     * Creates a new Value without derived
     * @param value
     * @return
     */
    public DataFieldCallBase<T> setValue(String value) {
        return setValue(new Value(value));
    }

    /**
     * Makes value immutable.
     * After the first time setValue is called subsequent calls do nothing.
     * @param value Value to be set to this Call object
     * @return
     */
    public DataFieldCallBase<T> setValue(Value value) {
        if(isValueSet) return this;

        this.value = value;
        isValueSet = true;
        return this;
    }

    public DataFieldCallBase<T> setChangeMap(Map<String, Change> changeMap) {
        this.changeMap = changeMap;
        return this;
    }

    public DataFieldCallBase<T> setInfo(DateTimeUserPair info) {
        this.info = info;
        return this;
    }

    public DataFieldCallBase<T> setLanguage(Language language) {
        this.language = language;
        return this;
    }
}
