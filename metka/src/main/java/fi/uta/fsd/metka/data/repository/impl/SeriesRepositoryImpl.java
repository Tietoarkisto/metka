package fi.uta.fsd.metka.data.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.FieldChange;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.factories.SeriesFactory;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository
public class SeriesRepositoryImpl implements SeriesRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private SeriesFactory factory;

    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Autowired
    private ConfigurationRepository configRepo;

    @Override
    public RevisionData getNew() throws IOException {
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

        SeriesEntity series = em.find(SeriesEntity.class, so.getSeriesno());
        if(series == null) {
            // There has to be a series so you can save
            return false;
        }

        // We can assume there is going to be latest revision since it is always required to exist.
        if(series.getCurApprovedNo() != null && series.getCurApprovedNo().equals(series.getLatestRevisionNo())) {
            // If latest revision is the same as current approved revision then it is not a draft and can not be saved
            return false;
        }

        //RevisionEntity revEntity = series.getLatestRevision();
        RevisionEntity revEntity = em.find(RevisionEntity.class, new RevisionKey(series.getId(), series.getLatestRevisionNo()));
        if(revEntity.getState() != RevisionState.DRAFT || StringUtils.isEmpty(revEntity.getData())) {
            // Only drafts can be saved and there has to be existing revision data
            // TODO: if we get here an error has to be logged since data is out of sync
            return false;
        }

        RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);
        Configuration config = configRepo.findConfiguration(data.getConfiguration());

        // Validate SeriesSingleSO against revision data:
        // Id should match id in revision data and key.
        // Revision should match revision in key
        // If previous abbreviation exists then so should match that, otherwise abort.
        // If name field has changed record the change otherwise do no change to name field.
        // Id description field has changed record the change otherwise do no change to description field.
        // TODO: automate validation using configuration, since all needed information is there.
        
        boolean changes = false;

        DateTime time = new DateTime();

        ValueFieldContainer field;
        ValueFieldContainer newField = null;
        Integer intValue;
        String stringValue;
        ValueFieldChange change;
        String key;

        // Check ID
        key = "seriesno";
        field = getValueFieldContainerFromRevisionData(data, key);
        intValue = extractIntegerSimpleValue(field);
        if(!so.getSeriesno().equals(intValue)) {
            // TODO: data is out of sync or someone tried to change the id, log error
            // Return false since save can not continue.
            return false;
        }
        
        // Check abbreviation.
        // TODO: handle immutable values (values that can be inserted but once inserted can not be changed).
        key = "seriesabb";
        field = getValueFieldContainerFromRevisionData(data, key); // Since we are in a DRAFT this returns a field only if one exists in changes
        stringValue = extractStringSimpleValue(field);
        if(!StringUtils.isEmpty(stringValue) && !so.getSeriesabb().equals(stringValue)) {
            // TODO: data is out of sync or someone tried to change the abbreviation, log error
            return false;
        }

        if(!StringUtils.isEmpty(so.getSeriesabb()) && StringUtils.isEmpty(stringValue)) {
            changes = true;

            newField = createValueFieldContainer(key, time);
            setSimpleValue(newField, so.getByKey(key).toString());

            change = updateValueField(data, key, newField);
            data.putChange(change);
        }

        // TODO: these following two can be generalised and used by the other type of objects too.
        // Check name
        doSingleValueChanges("seriesname", so, time, data, config);
        
        // Check description
        doSingleValueChanges("seriesdesc", so, time, data, config);

        // check series notes
        doSingleValueChanges("seriesnotes", so, time, data, config);

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

    /*
    * Approve series.
    * Modifies the revision data so that it is a correct approved data set, changes state to APPROVED
    * and then saves it back to database.
    *
    * TODO: change to use as much configuration as is possible e.g. check that immutable fields have not been changed
    *       or removed.
    */
    @Override
    public boolean approveSeries(Object seriesno) throws IOException {
        // Get series entity
        // Compare current approved and latest revision no, if they are the same there is nothing to approve.
        // If latest revision is larger than current approved then get the revision.
        // If latest revision is not in DRAFT state log exception and return false.
        // Deserialize revision data.

        SeriesEntity series = em.find(SeriesEntity.class, seriesno);

        if(series == null) {
            // TODO: log suitable error
            return false;
        }

        if(series.getCurApprovedNo() == null && series.getLatestRevisionNo() == null) {
            // TODO: log suitable error
            System.err.println("No revision found when approving series "+seriesno);
            return false;
        }

        if(series.getCurApprovedNo() != null && series.getCurApprovedNo().equals(series.getLatestRevisionNo())) {
            // Assume no DRAFT exists in this case. Add confirmation if necessary but it will still be an exception and
            // approval will not be done anyway.
            return true;
        }

        if(series.getCurApprovedNo() != null && series.getCurApprovedNo().compareTo(series.getLatestRevisionNo()) > 0) {
            // TODO: log exception since data is out of sync
            System.err.println("Current approved is larger than latest revision on series "+seriesno+". This should not happen.");
            return false;
        }

        //RevisionEntity entity = series.getLatestRevision();
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(series.getId(), series.getLatestRevisionNo()));
        if(entity.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Latest revision should be DRAFT but is not on series "+seriesno);
            return false;
        }

        RevisionData data = metkaObjectMapper.readValue(entity.getData(), RevisionData.class);

        // Check that data is also in DRAFT state and that id and revision match.
        // For each change:
        //          If the operation is unchanged then take the original value.
        //          If the operation is removed then take no value.
        //          If the operation is modified take the new value.
        // TODO: Validate changed value where necessary
        //      Put the result into fields map.
        //      If the operation is unchanged then remove the change object from the changes map.

        if(data.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Revision data on series "+seriesno+" was not in DRAFT state even though should have been.");
            return false;
        }

        if(!data.getKey().getId().equals(entity.getKey().getRevisionableId())
                || !data.getKey().getRevision().equals(entity.getKey().getRevisionNo())) {
            // TODO: log exception since data and entity keys don't match
            System.err.println("RevisionEntity and RevisionData keys do not match");
            System.err.println(data.getKey());
            System.err.println(entity.getKey());

            return false;
        }

        List<String> unchangedKeys = new ArrayList<String>();
        // TODO: Field type confirmation from configuration. For now assume accurate use
        for(String key : data.getChanges().keySet()) {
            ValueFieldChange change = (ValueFieldChange)data.getChange(key);
            FieldContainer field = null;
            if(change.getOperation() == ChangeOperation.UNCHANGED) {
                unchangedKeys.add(key);
                field = change.getOriginalField();
            } else if(change.getOperation() == ChangeOperation.MODIFIED) {
                field = change.getNewField();
            }
            if(field != null) {
                data.putField(field);
            }
        }
        for(String key : unchangedKeys) {
            data.getChanges().remove(key);
        }

        // Change state in revision data to approved.
        // Serialize data back to revision entity.
        // Change state of entity to approved.
        // Update current approved revision number on series entity
        // Entities should still be managed so no merge necessary.
        data.setState(RevisionState.APPROVED);
        data.setApprovalDate(new LocalDate());
        // TODO: set approver for the data to the user who requested the data approval
        entity.setData(metkaObjectMapper.writeValueAsString(data));
        entity.setState(RevisionState.APPROVED);
        series.setCurApprovedNo(series.getLatestRevisionNo());

        return true;
    }

    @Override
    public RevisionData editSeries(Object seriesno) throws IOException {
        // Get series entity
        // Do the usual checking
        // If latest revision differs from current approved get that
        // Do the usual checking
        // Return deserialized revision data from the already existing DRAFT
        SeriesEntity series = em.find(SeriesEntity.class, seriesno);

        if(series == null) {
            // TODO: log suitable error
            return null;
        }
        if(series.getCurApprovedNo() == null && series.getLatestRevisionNo() == null) {
            // TODO: log suitable error
            System.err.println("No revision found when trying to edit series "+seriesno);
            return null;
        }

        //RevisionEntity latestRevision = series.getLatestRevision();
        RevisionEntity latestRevision = em.find(RevisionEntity.class, new RevisionKey(series.getId(), series.getLatestRevisionNo()));
        RevisionData oldData = metkaObjectMapper.readValue(latestRevision.getData(), RevisionData.class);
        if(series.getCurApprovedNo() == null || series.getCurApprovedNo().compareTo(series.getLatestRevisionNo()) < 0) {
            if(latestRevision.getState() != RevisionState.DRAFT) {
                // TODO: log exception since data is out of sync
                System.err.println("Latest revision should be DRAFT but is not on series "+seriesno);
                return null;
            }
            if(oldData.getState() != RevisionState.DRAFT) {
                // TODO: log exception since data is out of sync
                System.err.println("Revision data on series "+seriesno+" was not in DRAFT state even though should have been.");
                return null;
            }
            return oldData;
        }

        // If not then create new revision
        // Increase revision number from latest revision
        // Set state to DRAFT
        // Generate initial data
        // Get latest revision
        // Go through fields map
        // For each field generate change with operation UNCHANGED and put the field to original value
        // Add changes to new dataset
        RevisionEntity newRevision = new RevisionEntity(new RevisionKey(latestRevision.getKey().getRevisionableId(),
                latestRevision.getKey().getRevisionNo()+1));
        newRevision.setState(RevisionState.DRAFT);
        RevisionData newData = RevisionData.createRevisionData(newRevision, oldData.getConfiguration());
        DateTime time = new DateTime();
        // TODO: Field type checking from configuration. For now treate everything as ValueField
        for(Map.Entry<String, FieldContainer> field : oldData.getFields().entrySet()) {
            ValueFieldChange change = createNewRevisionValueFieldChange((ValueFieldContainer)field.getValue());
            newData.putChange(change);
        }

        // Serialize new dataset to the new revision entity
        // Persist new entity
        newRevision.setData(metkaObjectMapper.writeValueAsString(newData));
        em.persist(newRevision);

        // Set latest revision number to new revisions revision number
        // No merge needed since entity still managed
        // Return new revision data
        series.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
        return newData;
    }
}
