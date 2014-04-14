package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.MiscJSONEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ReferenceRepository;
import fi.uta.fsd.metka.model.configuration.Reference;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

@Repository("referenceRepository")
public class ReferenceRepositoryImpl implements ReferenceRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

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
    }

    @Override
    public RevisionEntity getRevisionForReference(RevisionableEntity revisionable, Reference reference) {
        RevisionKey key = new RevisionKey(
                revisionable.getId(),
                (reference.getApprovedOnly()) ? revisionable.getCurApprovedNo() : revisionable.getLatestRevisionNo());
        RevisionEntity revision = em.find(RevisionEntity.class, key);
        return revision;
    }

    @Override
    public MiscJSONEntity getMiscJsonForReference(Reference reference) {
        MiscJSONEntity entity = em.find(MiscJSONEntity.class, reference.getTarget());
        return entity;
    }

    @Override
    public RevisionEntity getRevisionForReferencedRevisionable(Reference reference, String value) {
        Integer key = stringToInteger(value);
        if(key == null) {
            return null;
        }
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, key);
        if(revisionable == null) {
            return null;
        }

        RevisionKey revKey = new RevisionKey(
                revisionable.getId(),
                (reference.getApprovedOnly()) ? revisionable.getCurApprovedNo() : revisionable.getLatestRevisionNo());
        RevisionEntity revision = em.find(RevisionEntity.class, revKey);
        return revision;
    }
}
