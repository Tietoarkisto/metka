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
}
