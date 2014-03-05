package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository
public class StudyRepositoryImpl implements StudyRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private StudyFactory factory;

    @Autowired
    private JSONUtil json;

    @Autowired
    private ConfigurationRepository configRepo;

    @Override
    public RevisionData getNew(Integer acquisition_number) throws IOException {
        StudyEntity entity = new StudyEntity();
        em.persist(entity);

        RevisionEntity revision = new RevisionEntity(new RevisionKey(entity.getId(), 1));
        revision.setState(RevisionState.DRAFT);

        /*
         * creates initial dataset for the first draft any exceptions thrown should force rollback
         * automatically.
         * This assumes the entity has empty data field and is a draft.
        */
        RevisionData data = factory.newData(revision, acquisition_number);
        em.persist(revision);

        entity.setLatestRevisionNo(revision.getKey().getRevisionNo());

        return data;
    }

    @Override
    // TODO: needs better reporting to user about what went wrong
    public boolean saveStudy(TransferObject to) throws IOException {
        // Get StudyEntity
        // Check if latest revision is different from latest approved (the first requirement since only drafts
        // can be saved and these should always be different if Revisionable has an active draft).
        // Get latest revision.
        // Check state
        // Deserialize revision data and check that data agrees with entity.

        StudyEntity study = em.find(StudyEntity.class, to.getId());
        if(study == null) {
            // There has to be a series so you can save
            return false;
        }

        // We can assume there is going to be latest revision since it is always required to exist.
        if(study.getCurApprovedNo() != null && study.getCurApprovedNo().equals(study.getLatestRevisionNo())) {
            // If latest revision is the same as current approved revision then it is not a draft and can not be saved
            return false;
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, new RevisionKey(study.getId(), study.getLatestRevisionNo()));
        if(revEntity.getState() != RevisionState.DRAFT || StringUtils.isEmpty(revEntity.getData())) {
            // Only drafts can be saved and there has to be existing revision data
            // TODO: if we get here an error has to be logged since data is out of sync
            return false;
        }

        // Get old revision data and its configuration.
        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());

        // Validate StudySingleSO against revision data:
        // Id should match id in revision data and key.
        // Revision should match revision in key
        // Study_number should match that of existing revision since it's set at first creation.

        // Check ID integrity
        if (idIntegrityCheck(to, data, config)) {
            return false;
        }

        // check revision
        if(!to.getRevision().equals(data.getKey().getRevision())) {
            // TODO: data is out of sync or someone tried to change the revision, log error
            // Return false since save can not continue.
            return false;
        }

        // Check values

        boolean changes = false;

        DateTime time = new DateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            data.setLastSave(new LocalDate());
            revEntity.setData(json.serialize(data));
        }

        return true;
    }
}