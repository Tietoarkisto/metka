package fi.uta.fsd.metka.storage.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
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
    public void insert(JsonNode misc) {
        JsonNode key = misc.get("key");
        if(key == null || key.getNodeType() != JsonNodeType.STRING) {
            // Not key or key is not text, ignore
            return;
        }
        if(misc.get("data") == null || misc.get("data").getNodeType() != JsonNodeType.ARRAY) {
            // No data or data not an array, ignore
            return;
        }
        ArrayNode data = (ArrayNode)misc.get("data");
        if(data.size() <= 0) {
            // No actual data, ignore.
            return;
        }

        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key.asText());
        if(entity == null) {
            entity = new MiscJSONEntity();
            entity.setKey(misc.get("key").asText());
            em.persist(entity);
        }
        entity.setData(misc.toString());
    }

    @Override
    public void insert(String text) {
        JsonNode node = json.readJsonTree(text);
        insert(node);
    }

    @Override
    public void merge(JsonNode misc) {
        // TODO: actual merge
        insert(misc);
    }

    @Override
    public JsonNode findByKey(String key) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key);
        if(entity == null || StringUtils.isEmpty(entity.getData())) {
            return null;
        }
        return json.readJsonTree(entity.getData());
    }
}
