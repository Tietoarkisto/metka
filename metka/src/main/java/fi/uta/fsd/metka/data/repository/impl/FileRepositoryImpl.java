package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.FileEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.FileRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.factories.FileFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

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

        RevisionEntity revision = new RevisionEntity(new RevisionKey(entity.getId(), 1));
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

    /**
     * Return latest revision data for a FILE with given revisionable id.
     * @param id Revisionable id
     * @return Latest RevisionableData for given id, this should always exist assuming that given id is for a FILE
     * @throws IOException
     */
    @Override
    public RevisionData findLatestRevision(Integer id) throws IOException {
        RevisionableEntity file = em.find(RevisionableEntity.class, id);

        // Sanity check
        if(file == null || ConfigurationType.fromValue(file.getType()) != ConfigurationType.FILE || file.getLatestRevisionNo() == null) {
            // TODO: Log error, this should never happen.
            return null;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(file.getId(), file.getLatestRevisionNo()));
        // another sanity check
        if(revision == null || StringUtils.isEmpty(revision.getData())) {
            // TODO: Log error since something is seriously wrong.
            return null;
        }

        RevisionData data = json.readRevisionDataFromString(revision.getData());
        return data;
    }

    @Override
    public SavedReference saveAndApprove(TransferObject to) throws Exception {
        FileEntity file = em.find(FileEntity.class, to.getId());
        if(file == null) {
            // There has to be a file so you can save
            return null;
        }

        // Sanity check
        if(file.getLatestRevisionNo() == null) {
            // TODO: There's no latest revision, something is really wrong, log error.
            return null;
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, new RevisionKey(file.getId(), file.getLatestRevisionNo()));
        if(revEntity == null) {
            // TODO: Log error, revision should excist
            return null;
        }

        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
        Configuration config = configRepo.findLatestConfiguration(ConfigurationType.FILE);


        if (idIntegrityCheck(to, data, config)) {
            throw new Exception("Id integrity was not maintained");
        }

        if(data.getState() != RevisionState.DRAFT) {
            // This is not the first time this file is saved, make a new revision and make data for it.
            revEntity = new RevisionEntity(new RevisionKey(file.getId(), file.getLatestRevisionNo()+1));
            revEntity.setState(RevisionState.DRAFT);

            em.persist(revEntity);

            file.setLatestRevisionNo(revEntity.getKey().getRevisionNo());

            // Create new RevisionData object using the current revEntity (either new or old, doesn't matter)
            RevisionData newData = RevisionData.createRevisionData(revEntity, config.getKey());
            // Copy old fields to new revision data
            for(DataField field : data.getFields().values()) {
                newData.putField(field.copy());
            }
            // Changes are not copied but instead changes in oldData are used to normalize changes in fields copied to
            // newData since changes in previous revision are original values in new revision.
            // Go through changes and move modified value to original value for every change.
            changesToOriginals(data.getChanges(), newData.getFields());
            newData.setState(RevisionState.APPROVED);
            newData.setApprovalDate(new DateTime());
            newData.setLastSave(new DateTime());

            revEntity.setData(json.serialize(newData));
        } else {
            // This was the first time the user has manually saved this file. We can use the current revision and data, just approve it
            data.setState(RevisionState.APPROVED);
            data.setApprovalDate(new DateTime());
            data.setLastSave(new DateTime());

            revEntity.setData(json.serialize(data));
        }

        revEntity.setState(RevisionState.APPROVED);
        return null;
    }
}
