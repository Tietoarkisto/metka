package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ReferenceRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository("referenceRepository")
public class ReferenceRepositoryImpl implements ReferenceRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public List<ReferenceOption> collectReferenceOptions(Field field, Reference reference) throws IOException {
        List<ReferenceOption> options = new ArrayList<>();
        if(reference == null) {
            // TODO: Possibly needs to log an event since something has lead to a request that should not have been made
            // Return the empty list since we can not find values for a non existing reference
            return options;
        }

        // It's assumed that given field configuration is correct for given reference.

        // Distinguish between reference types and forward the request to separate handlers.
        switch(reference.getType()) {
            case REVISIONABLE:
                revisionableReferenceHandler(field, reference, options);
                break;
            case JSON:
            case DEPENDENCY:
                // TODO: Handle JSON and DEPENDENCY
                break;
        }

        return options;
    }

    /**
     * Analyses a revisionable reference and collect the values defined by that reference.
     *
     * TODO: At the moment handles only titlePaths of top level non container fields. When others are handled it should be made sure that the path actually terminates on a value, not an object of collection
     * @param field Field configuration that uses the given reference
     * @param reference Reference to be processed
     * @param options List where found values are placed as ReferenceOption objects
     */
    private void revisionableReferenceHandler(Field field, Reference reference, List<ReferenceOption> options)
            throws IOException {
        // Make sure that reference target is an actual type
        if(!ConfigurationType.isValue(reference.getTarget())) {
            return;
        }
        List<RevisionableEntity> entities =
                em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.type=:type AND r.removed=:removed", RevisionableEntity.class)
                .setParameter("type", reference.getTarget())
                .setParameter("removed", false)
                .getResultList();

        for(RevisionableEntity entity : entities) {
            if(reference.getApprovedOnly() && entity.getCurApprovedNo() == null) {
                // No approved revision, not applicable.
                continue;
            }
            String title = null;
            if(!StringUtils.isEmpty(reference.getTitlePath())) {
                // Title is requested
                RevisionEntity revision = em.find(
                        RevisionEntity.class,
                        new RevisionKey(
                                entity.getId(),
                                (reference.getApprovedOnly()) ? entity.getCurApprovedNo() : entity.getLatestRevisionNo()));
                if(revision == null || StringUtils.isEmpty(revision.getData())) {
                    // TODO: There's a data problem, log event
                    continue;
                }

                RevisionData data = json.readRevisionDataFromString(revision.getData());
                SavedDataField saved = getSavedDataFieldFromRevisionData(data, reference.getTitlePath());
                if(saved != null) {
                    title = saved.getActualValue();
                }
            }
            if(title == null) {
                title = entity.getId().toString();
            }
            options.add(new ReferenceOption(entity.getId().toString(), title));
        }
    }
}
