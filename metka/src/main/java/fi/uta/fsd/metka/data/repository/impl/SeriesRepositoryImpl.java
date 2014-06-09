package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.SeriesFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository
public class SeriesRepositoryImpl implements SeriesRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private SeriesFactory factory;

    @Autowired
    private ConfigurationRepository configRepo;

    @Autowired
    private GeneralRepository general;

    @Override
    public RevisionData getNew() throws IOException {
        SeriesEntity entity = new SeriesEntity();
        em.persist(entity);

        RevisionEntity revision = entity.createNextRevision();

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
    public boolean saveSeries(TransferObject to) throws IOException {
        // Get SeriesEntity
        // Check if latest revision is different from latest approved (the first requirement since only drafts
        // can be saved and these should always be different if Revisionable has an active draft).
        // Get latest revision.
        // Check state
        // Deserialize revision data and check that data agrees with entity.

        SeriesEntity series = em.find(SeriesEntity.class, to.getId());
        if(series == null) {
            // There has to be a series so you can save
            return false;
        }

        if(!series.hasDraft()) {
            // There's no draft according to simple check, assume this is correct. There's nothing to save.
            // TODO: Log event that something tried to save a nonexisting draft
            return false;
        }

        //RevisionEntity revEntity = series.getLatestRevision();
        RevisionEntity revEntity = em.find(RevisionEntity.class, series.latestRevisionKey());
        if(revEntity.getState() != RevisionState.DRAFT || StringUtils.isEmpty(revEntity.getData())) {
            // Only drafts can be saved and there has to be existing revision data
            // TODO: if we get here an error has to be logged since data is out of sync
            return false;
        }

        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());

        // Validate TransferObject against revision data:
        // Id should match id in revision data and key.
        // Revision should match revision in key
        // If previous abbreviation exists then so should match that, otherwise abort.
        // If name field has changed record the change otherwise do no change to name field.
        // Id description field has changed record the change otherwise do no change to description field.
        // TODO: automate validation using configuration, since all needed information is there.
        
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

        boolean changes = false;

        LocalDateTime time = new LocalDateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            data.setLastSave(new LocalDateTime());
            revEntity.setData(json.serialize(data));
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

        if(!series.hasDraft()) {
            // Assume no DRAFT exists in this case. Add confirmation if necessary but it will still be an exception and
            // approval will not be done anyway.
            return true;
        }

        RevisionEntity entity = em.find(RevisionEntity.class, series.latestRevisionKey());
        if(entity.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Latest revision should be DRAFT but is not on series "+seriesno);
            return false;
        }

        RevisionData data = json.readRevisionDataFromString(entity.getData());

        // Check that data is also in DRAFT state and that id and revision match.
        // For each change:
        //          If the operation is unchanged then take the original value.
        //          If the operation is removed then take no value.
        //          If the operation is modified take the new value.
        // TODO: Validate changed value where necessary

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

        // Change state in revision data to approved.
        // Serialize data back to revision entity.
        // Change state of entity to approved.
        // Update current approved revision number on series entity
        // Entities should still be managed so no merge necessary.
        data.setState(RevisionState.APPROVED);
        data.setApprovalDate(new LocalDateTime());
        // TODO: set approver for the data to the user who requested the data approval
        entity.setData(json.serialize(data));
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

        //RevisionEntity latestRevision = series.getLatestRevision();
        RevisionEntity latestRevision = em.find(RevisionEntity.class, series.latestRevisionKey());
        RevisionData oldData = json.readRevisionDataFromString(latestRevision.getData());
        if(series.hasDraft()) {
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
        RevisionEntity newRevision = series.createNextRevision();
        RevisionData newData = DataFactory.createNewRevisionData(newRevision, oldData);

        // Serialize new dataset to the new revision entity
        // Persist new entity
        newRevision.setData(json.serialize(newData));
        em.persist(newRevision);

        // Set latest revision number to new revisions revision number
        // No merge needed since entity still managed
        // Return new revision data
        series.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
        return newData;
    }
}
