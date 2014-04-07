package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.MiscJSONEntity;
import fi.uta.fsd.metka.data.repository.MiscJSONRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import org.json.JSONObject;
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
    public void insert(JSONObject misc) {
        if(misc.opt("key") == null || misc.opt("data") == null || misc.getJSONArray("data").length() == 0) {
            // Not key and no data, ignore
            return;
        }

        MiscJSONEntity entity = em.find(MiscJSONEntity.class, misc.getString("key"));
        if(entity == null) {
            entity = new MiscJSONEntity();
            entity.setKey(misc.getString("key"));
            em.persist(entity);
        }
        entity.setData(misc.toString());
    }

    @Override
    public void merge(JSONObject misc) {
        // TODO: actual merge
        insert(misc);
    }

    @Override
    public JSONObject findByKey(String key) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key);
        if(entity == null || StringUtils.isEmpty(entity.getData())) {
            return null;
        }
        return new JSONObject(entity.getData());
    }
}
