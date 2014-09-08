package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ReferenceRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
