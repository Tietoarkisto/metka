package fi.uta.fsd.metka.storage.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ReferenceRepository;
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
import java.util.List;

@Repository("referenceRepository")
public class ReferenceRepositoryImpl implements ReferenceRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public List<RevisionableEntity> getRevisionablesForReference(Reference reference) {
        // Make sure that reference target is an actual type
        if(!ConfigurationType.isValue(reference.getTarget())) {
            return null;
        }
        List<RevisionableEntity> entities =
                em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.type=:type AND r.removed=:removed", RevisionableEntity.class)
                        .setParameter("type", reference.getTarget())
                        .setParameter("removed", false)
                        .getResultList();
        return entities;
    }/*

    @Override
    public RevisionEntity getRevisionForReference(RevisionableEntity revisionable, Reference reference) {
        if(reference.getApprovedOnly() && revisionable.getCurApprovedNo() == null) {
            return null;
        }
        RevisionKey key = (reference.getApprovedOnly()) ? revisionable.currentApprovedRevisionKey() : revisionable.latestRevisionKey();
        RevisionEntity revision = em.find(RevisionEntity.class, key);
        return revision;
    }*/

    @Override
    public Pair<ReturnResult, JsonNode> getMiscJson(String key) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, key);
        if(entity == null || !StringUtils.hasText(entity.getData())) {
            // No json or no data, can't continue
            return new ImmutablePair<>(ReturnResult.MISC_JSON_NOT_FOUND, null);
        }

        Pair<SerializationResults, JsonNode> pair = json.deserializeToJsonTree(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            // No root node, can't continue
            return new ImmutablePair<>(ReturnResult.MISC_JSON_NOT_FOUND, null);
        }

        return new ImmutablePair<>(ReturnResult.MISC_JSON_FOUND, pair.getRight());
    }

    @Override
    public MiscJSONEntity getMiscJsonForReference(Reference reference) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, reference.getTarget());
        return entity;
    }/*



    @Override
    public RevisionEntity getRevisionForReferencedRevisionable(Reference reference, String value) {
        Long key = stringToLong(value);
        if(key == null) {
            return null;
        }
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, key);
        if(revisionable == null) {
            return null;
        }
        return getRevisionForReference(revisionable, reference);
    }*/
}
