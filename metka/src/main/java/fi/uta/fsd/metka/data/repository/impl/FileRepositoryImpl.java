package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.FileLinkQueueEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.FileEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.enums.VariableDataType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.FileRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.factories.FileFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository
public class FileRepositoryImpl implements FileRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private FileFactory factory;

    @Autowired
    private ConfigurationRepository configRepo;

    @Autowired
    private JSONUtil json;

    @Override
    public RevisionData newFileRevisionable(String path) throws IOException {
        FileEntity entity = new FileEntity();
        em.persist(entity);

        RevisionEntity revision = entity.createNextRevision();
        revision.setState(RevisionState.DRAFT);

        /*
         * creates initial dataset for the first draft any exceptions thrown should force rollback
         * automatically.
         * This assumes the entity has empty data field and is a draft.
        */
        RevisionData data = factory.newData(revision, path);
        em.persist(revision);

        entity.setLatestRevisionNo(revision.getKey().getRevisionNo());

        return data;
    }


    @Override
    public RevisionData getEditableRevision(Integer id) throws IOException {
        RevisionableEntity file = em.find(RevisionableEntity.class, id);

        // Sanity check
        if(file == null || ConfigurationType.fromValue(file.getType()) != ConfigurationType.FILE || file.getLatestRevisionNo() == null) {
            // TODO: Log error, this should never happen.
            return null;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(file.getId(), file.getLatestRevisionNo()));
        if(revision == null) {
            // TODO: Missing revision, log error
            return null;
        }
        RevisionData data = json.readRevisionDataFromString(revision.getData());
        if(data == null) {
            // TODO: Log error, missing data
            return null;
        }
        // If DRAFT exists, return it
        if(file.getCurApprovedNo() == null || !file.getCurApprovedNo().equals(file.getLatestRevisionNo())) {
            // Sanity check that latest revision is indeed a DRAFT
            if(revision.getState() != RevisionState.DRAFT || data.getState() != RevisionState.DRAFT) {
                // TODO: Log error, there is a data discrepancy
                return null;
            }

            return data;
        } else {
            // Latest revision should be APPROVED, make sanity check and if passed create a new DRAFT
            if(revision.getState() != RevisionState.APPROVED || data.getState() != RevisionState.APPROVED) {
                // TODO: Log error, there is a data discrepancy
                return null;
            }

            RevisionEntity newRevision = file.createNextRevision();
            newRevision.setState(RevisionState.DRAFT);
            em.persist(newRevision);
            file.setLatestRevisionNo(newRevision.getKey().getRevisionNo());

            // Get latest configuration for configuration key for new data
            Configuration config = configRepo.findLatestConfiguration(ConfigurationType.FILE);

            // Create new RevisionData object using the current revEntity (either new or old, doesn't matter)
            RevisionData newData = RevisionData.createRevisionData(newRevision, config.getKey());
            // Copy old fields to new revision data
            for(DataField field : data.getFields().values()) {
                newData.putField(field.copy());
            }
            // Changes are not copied but instead changes in oldData are used to normalize changes in fields copied to
            // newData since changes in previous revision are original values in new revision.
            // Go through changes and move modified value to original value for every change.
            changesToOriginals(data.getChanges(), newData.getFields());
            newRevision.setData(json.serialize(newData));

            return newData;
        }
    }

    @Override
    public void saveAndApprove(TransferObject to) throws Exception {
        FileEntity file = em.find(FileEntity.class, to.getId());
        if(file == null) {
            // There has to be a file so you can save
            throw new Exception("No file found for id "+to.getId());
        }

        // Sanity check
        if(file.getLatestRevisionNo() == null) {
            // TODO: There's no latest revision, something is really wrong, log error.
            throw new Exception("No revision found for id "+to.getId());
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, new RevisionKey(file.getId(), file.getLatestRevisionNo()));
        if(revEntity == null) {
            // TODO: Log error, revision should excist
            throw new Exception("No revision found for id "+to.getId() + " and revision "+file.getLatestRevisionNo());
        }
        if(revEntity.getState() != RevisionState.DRAFT) {
            // TODO: Log error, revision should be a draft
            throw new Exception("Revision is not a DRAFT");
        }

        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
        Configuration config = configRepo.findLatestConfiguration(ConfigurationType.FILE);

        // Latest data is not a DRAFT
        if(data.getState() != RevisionState.DRAFT) {
            // TODO: Log error, data discrepancy
            throw new Exception("Data is not a DRAFT");
        }

        if (idIntegrityCheck(to, data, config)) {
            throw new Exception("Id integrity was not maintained");
        }

        // Check values

        boolean changes = false;

        DateTime time = new DateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        if(changes) {
            // If there were changes save and approve current revision.
            data.setState(RevisionState.APPROVED);
            data.setApprovalDate(new DateTime());
            data.setLastSave(new DateTime());

            revEntity.setData(json.serialize(data));
            revEntity.setState(RevisionState.APPROVED);
            file.setCurApprovedNo(revEntity.getKey().getRevisionNo());
        }
    }

    @Override
    public void addFileLinkEvent(Integer targetId, Integer fileId, String key, String path) {
        FileLinkQueueEntity fileLink = new FileLinkQueueEntity();
        fileLink.setTargetId(targetId);
        fileLink.setTargetField(key);
        fileLink.setFileId(fileId);
        fileLink.setPath(path);
        if(FilenameUtils.getExtension(path).toUpperCase().equals("POR")) {
            fileLink.setType(VariableDataType.POR);
        } else {
            fileLink.setType(null);
        }
        em.persist(fileLink);
    }
}
