package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.SeriesFactory;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.SeriesRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SeriesRepositoryImpl implements SeriesRepository {
    private static Logger logger = LoggerFactory.getLogger(SeriesRepositoryImpl.class);
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
    public RevisionData getNew() {
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

    /*
    * Approve series.
    * Modifies the revision data so that it is a correct approved data set, changes state to APPROVED
    * and then saves it back to database.
    *
    * TODO: change to use as much configuration as is possible e.g. check that immutable fields have not been changed
    *       or removed.
    */
    @Override
    public boolean approveSeries(Object seriesno) {
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

        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        RevisionData data = json.deserializeRevisionData(entity.getData()).getRight();

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
                || !data.getKey().getNo().equals(entity.getKey().getRevisionNo())) {
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
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        entity.setData(json.serialize(data).getRight());
        entity.setState(RevisionState.APPROVED);
        series.setCurApprovedNo(series.getLatestRevisionNo());

        return true;
    }

    @Override
    public RevisionData editSeries(Object seriesno) {
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
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        RevisionData oldData = json.deserializeRevisionData(latestRevision.getData()).getRight();
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
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        newRevision.setData(json.serialize(newData).getRight());
        em.persist(newRevision);

        // Set latest revision number to new revisions revision number
        // No merge needed since entity still managed
        // Return new revision data
        series.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
        return newData;
    }
}
