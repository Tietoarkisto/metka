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
    private ConfigurationRepository configRepo;

    @Autowired
    private GeneralRepository general;

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
