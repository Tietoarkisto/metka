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

package fi.uta.fsd.metka.storage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Handles general JSON deserialization and serialization operations.
 */
@Service
public final class JSONUtil {

    // Private constructor to stop instantiation
    private JSONUtil() {}

    @Autowired
    private ObjectMapper metkaObjectMapper;

    public Pair<SerializationResults, Configuration> deserializeDataConfiguration(File file) {
        return deserializeFromFile(file, Configuration.class);
    }
    public Pair<SerializationResults, Configuration> deserializeDataConfiguration(String data) {
        return deserializeFromString(data, Configuration.class);
    }

    public Pair<SerializationResults, GUIConfiguration> deserializeGUIConfiguration(File file) {
        return deserializeFromFile(file, GUIConfiguration.class);
    }
    public Pair<SerializationResults, GUIConfiguration> deserializeGUIConfiguration(String data) {
        return deserializeFromString(data, GUIConfiguration.class);
    }

    public Pair<SerializationResults, RevisionData> deserializeRevisionData(String data) {
        return deserializeFromString(data, RevisionData.class);
    }

    public Pair<SerializationResults, TransferData> deserializeTransferData(String data) {
        return deserializeFromString(data, TransferData.class);
    }

    private <T extends ModelBase> Pair<SerializationResults, T> deserializeFromString(String data, Class<T> tClass) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(data, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while parsing " + tClass.toString() + " from string data");
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    private <T extends ModelBase> Pair<SerializationResults, T> deserializeFromFile(File file, Class<T> tClass) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(file, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while parsing "+tClass.toString()+" from file "+file.getName());
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, String> serialize(ModelBase data) {
        try {
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while serializing "+data.toString());
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, String> serialize(JsonNode data) {
        try {
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while serializing JsonNode");
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, JsonNode> deserializeToJsonTree(File file) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(file));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while reading file "+file.getName());
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, JsonNode> deserializeToJsonTree(String data) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while reading String");
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }
}
