package fi.uta.fsd.metka.data.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySingleSO;
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
    private ObjectMapper metkaObjectMapper;

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
    public boolean saveStudy(StudySingleSO so) throws IOException {
        // Get StudyEntity
        // Check if latest revision is different from latest approved (the first requirement since only drafts
        // can be saved and these should always be different if Revisionable has an active draft).
        // Get latest revision.
        // Check state
        // Deserialize revision data and check that data agrees with entity.

        StudyEntity study = em.find(StudyEntity.class, so.getStudy_id());
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
        RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);
        Configuration config = configRepo.findConfiguration(data.getConfiguration());

        // Validate StudySingleSO against revision data:
        // Id should match id in revision data and key.
        // Revision should match revision in key
        // Study_number should match that of existing revision since it's set at first creation.

        boolean changes = false;

        DateTime time = new DateTime();

        ValueFieldContainer field;
        ValueFieldContainer newField = null;
        Integer intValue;
        String stringValue;
        ValueFieldChange change;
        String key;

        // check revision
        if(!so.getRevision().equals(data.getKey().getRevision())) {
            // TODO: data is out of sync or someone tried to change the revision, log error
            // Return false since save can not continue.
            return false;
        }

        // Check ID
        key = "study_id";
        field = getValueFieldContainerFromRevisionData(data, key);
        intValue = extractIntegerSimpleValue(field);
        if(!so.getStudy_id().equals(intValue)) {
            // TODO: data is out of sync or someone tried to change the study_id, log error
            // Return false since save can not continue.
            return false;
        }

        // Check ID
        // TODO: add change checking for cases that should always be present and should not change.
        key = "id";
        field = getValueFieldContainerFromRevisionData(data, key);
        stringValue = extractStringSimpleValue(field);
        if(!so.getStudy_id().equals(intValue)) {
            // TODO: data is out of sync or someone tried to change the study_number, log error
            // Return false since save can not continue.
            return false;
        }

         // TODO: these following fields can be generalised and used by the other type of objects too.
        // Check study_name
        if(doSingleValueChanges("title", so, time, data, config)) {
            changes = true;
        }

        // TODO: Choicelist values should really check if the selected value is an existing value, approval should also check that the value is not depricated
        // TODO: For now types are assumed and hard coded so you can easily insert wrong values
        // Check study_type
        if(doSingleValueChanges("datakind", so, time, data, config)) {
            changes = true;
        }

        // Check approved
        if(doSingleValueChanges("ispublic", so, time, data, config)) {
            changes = true;
        }

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            data.setLastSave(new LocalDate());
            revEntity.setData(metkaObjectMapper.writeValueAsString(data));
        }

        return true;
    }
}