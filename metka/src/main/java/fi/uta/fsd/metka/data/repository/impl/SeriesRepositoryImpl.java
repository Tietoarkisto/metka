package fi.uta.fsd.metka.data.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.data.Change;
import fi.uta.fsd.metka.model.data.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.SeriesFactory;
import fi.uta.fsd.metka.mvc.MetkaObjectMapper;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/3/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class SeriesRepositoryImpl implements SeriesRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private SeriesFactory factory;

    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Override
    public RevisionData getNew()
            throws JsonProcessingException, JsonMappingException, IOException {
        SeriesEntity entity = new SeriesEntity();
        em.persist(entity);

        RevisionEntity revision = new RevisionEntity(new RevisionKey(entity.getId(), 1));
        revision.setState(RevisionState.DRAFT);

        /*
         * creates initial dataset for the first draft any exceptions thrown should force rollback
         * automatically.
         * This assumes the entity has empty data field and is a draft.
        */
        RevisionData data = factory.newData(revision);
        em.persist(revision);

        entity.setLatestRevisionNo(revision.getKey().getRevisionNo());

        return data;
    }

    @Override
    // TODO: needs better reporting to user about what went wrong
    public boolean saveSeries(SeriesSingleSO so) throws IOException {
        // Get SeriesEntity
        // Check if latest revision is different from latest approved (the first requirement since only drafts
        // can be saved and these should always be different if Revisionable has an active draft).
        // Get latest revision.
        // Check state
        // Deserialize revision data and check that data agrees with entity.

        List<SeriesEntity> temp = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();

        SeriesEntity series = em.find(SeriesEntity.class, so.getId());
        if(series == null) {
            // There has to be a series so you can save
            return false;
        }

        // We can assume there is going to be latest revision since it is always required to exist.
        if(series.getCurApprovedNo() != null && series.getCurApprovedNo().equals(series.getLatestRevisionNo())) {
            // If latest revision is the same as current approved revision then it is not a draft and can not be saved
            return false;
        }

        RevisionEntity revEntity = series.getLatestRevision();
        if(revEntity.getState() != RevisionState.DRAFT || StringUtils.isEmpty(revEntity.getData())) {
            // Only drafts can be saved and there has to be existing revision data
            // TODO: if we get here an error has to be logged since data is out of sync
            return false;
        }

        RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);

        // Validate SeriesSingleSO against revision data:
        // Id should match id in revision data.
        // If previous abbreviation exists then so should match that, otherwise abort.
        // If name field has changed record the change otherwise do no change to name field.
        // Id description field has changed record the change otherwise do no change to description field.

        boolean changes = false;

        DateTime time = new DateTime();

        FieldContainer field;
        Integer intValue;
        String stringValue;

        field = getContainerFromRevisionData(data, "id");
        intValue = extractIntegerValue(field);
        if(!so.getId().equals(intValue)) {
            // TODO: data is out of sync, log error
            // Return false since save can not continue.
            return false;
        }

        field = getContainerFromRevisionData(data, "abbreviation");
        stringValue = extractStringValue(field);
        if(!StringUtils.isEmpty(stringValue) && !so.getAbbreviation().equals(stringValue)) {
            // TODO: someone tried to change existing abbreviation, log error
            return false;
        }

        if(!so.getAbbreviation().equals(stringValue)) {
            changes = true;
            Change change = updateValue(data, time, "abbreviation", so.getAbbreviation());
            data.getChanges().put(change.getKey(), change);
        }

        field = getContainerFromRevisionData(data, "name");
        stringValue = extractStringValue(field);
        if(!so.getName().equals(stringValue)) {
            changes = true;

            Change change = updateValue(data, time, "name", so.getName());
            data.getChanges().put(change.getKey(), change);
        }

        field = getContainerFromRevisionData(data, "description");
        stringValue = extractStringValue(field);
        if(!so.getDescription().equals(stringValue)) {
            changes = true;

            Change change = updateValue(data, time, "description", so.getDescription());
            data.getChanges().put(change.getKey(), change);
        }

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            revEntity.setData(metkaObjectMapper.writeValueAsString(data));
        }

        return true;
    }

    private Change updateValue(RevisionData data, DateTime time, String key, String newValue) {
        Change change = data.getChanges().get(key);
        if(change == null) {
            change = factory.createSimpleChange(key, time);
        } else {
            change.setChangeTime(time);
        }
        if(StringUtils.isEmpty(newValue)) {
            change.setOperation(ChangeOperation.REMOVED);
            change.setNewField(null);
        } else {
            FieldContainer field = new FieldContainer(change.getKey());
            factory.setSimpleValue(field, newValue);

            change.setOperation(ChangeOperation.MODIFIED);
            change.setNewField(field);
        }
        // TODO: set user reference for who made the change
        return change;
    }
}
