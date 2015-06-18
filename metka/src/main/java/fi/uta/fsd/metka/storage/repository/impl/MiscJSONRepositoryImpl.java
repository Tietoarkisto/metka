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

package fi.uta.fsd.metka.storage.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository("miscJSONRepository")
public class MiscJSONRepositoryImpl implements MiscJSONRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;
    @Autowired
    private JSONUtil json;

    @Override
    public ReturnResult insert(JsonNode misc) {
        JsonNode key = misc.get("key");
        if(key == null || key.getNodeType() != JsonNodeType.STRING) {
            // Not key or key is not text, ignore
            return ReturnResult.PARAMETERS_MISSING;
        }
        if(misc.get("data") == null || misc.get("data").getNodeType() != JsonNodeType.ARRAY) {
            // No data or data not an array, ignore
            return ReturnResult.PARAMETERS_MISSING;
        }
        ArrayNode data = (ArrayNode)misc.get("data");
        if(data.size() <= 0) {
            // No actual data, ignore.
            return ReturnResult.PARAMETERS_MISSING;
        }

        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key.asText());
        if(entity == null) {
            entity = new MiscJSONEntity();
            entity.setKey(misc.get("key").asText());
            em.persist(entity);
        }
        entity.setData(misc.toString());
        return ReturnResult.DATABASE_INSERT_SUCCESS;
    }

    @Override
    public ReturnResult insert(String text) {
        Pair<SerializationResults, JsonNode> node = json.deserializeToJsonTree(text);
        if(node.getLeft() == SerializationResults.DESERIALIZATION_SUCCESS) {
            return insert(node.getRight());
        } else {
            return ReturnResult.OPERATION_FAIL;
        }
    }

    @Override
    public void merge(JsonNode misc) {
        // TODO: actual merge
        insert(misc);
    }

    @Override
    public Pair<ReturnResult, JsonNode> findByKey(String key) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key);
        if(entity == null || !StringUtils.hasText(entity.getData())) {
            return new ImmutablePair<>(ReturnResult.MISC_JSON_NOT_FOUND, null);
        }
        Pair<SerializationResults, JsonNode> pair = json.deserializeToJsonTree(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            return new ImmutablePair<>(ReturnResult.MISC_JSON_NOT_FOUND, null);
        } else {
            return new ImmutablePair<>(ReturnResult.MISC_JSON_FOUND, pair.getRight());
        }
    }

    @Override
    public List<String> getJsonKeys() {
        List<MiscJSONEntity> entities = em.createQuery("SELECT e FROM MiscJSONEntity e", MiscJSONEntity.class).getResultList();
        List<String> keys = new ArrayList<>();
        for(MiscJSONEntity entity : entities) {
            keys.add(entity.getKey());
        }
        return keys;
    }

    @Override
    public Pair<ReturnResult, String> findStringByKey(String key) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key);
        if(entity == null || !StringUtils.hasText(entity.getData())) {
            return new ImmutablePair<>(ReturnResult.MISC_JSON_NOT_FOUND, null);
        } else {
            return new ImmutablePair<>(ReturnResult.MISC_JSON_FOUND, entity.getData());
        }
    }
}
