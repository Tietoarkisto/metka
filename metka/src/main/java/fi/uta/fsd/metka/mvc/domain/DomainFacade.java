package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 10:07 AM
 */
@Component("domainFacade")
public class DomainFacade {

    @Autowired
    private CRUDRepository<RevisionableEntity, Integer> revisionableRepository;
    @Autowired
    private CRUDRepository<StudyEntity, String> studyRepository;
    @Autowired
    private CRUDRepository<RevisionEntity, RevisionKey> revisionRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;

    public StudyEntity createStudy() {
        StudyEntity study = studyRepository.create(new StudyEntity());
        addDraft(study);
        return study;
    }

    public List<StudyEntity> listAllStudies() {
        return studyRepository.listAll();
    }

    public List<RevisionEntity> listAllRevisions() {
        return revisionRepository.listAll();
    }

    public RevisionEntity addDraft(RevisionableEntity entity) {
        entity = revisionableRepository.read(entity.getId());
        if(entity.getLatestRevisionNo() != entity.getCurApprovedNo()) {
            return revisionRepository.read((new RevisionKey(entity.getId(), entity.getLatestRevisionNo())));
        } else {
            RevisionEntity revision = new RevisionEntity(new RevisionKey(entity.getId(), (entity.getLatestRevisionNo() != null)?entity.getLatestRevisionNo()+1:1));
            revision.setState(RevisionState.DRAFT);
            revisionRepository.create(revision);
            entity.setLatestRevisionNo(revision.getKey().getRevisionNo());
            revisionableRepository.update(entity);
            return revision;
        }
    }

    public RevisionEntity approveDraft(RevisionEntity entity) {
        if(entity.getState().equals(RevisionState.DRAFT)) {
            // Here all JSON changes for approval should be made, also all validation related to
            // approval depending on the type of revision.

            // Most likely some kind of approval utility is needed that could be called with the new data and it would
            // validate the dataset for approval.
            RevisionableEntity revisionable = revisionableRepository.read(entity.getKey().getRevisionableId());
            if(revisionable == null) {
                // Something is wrong here, you should not be able to touch revisions without revisionable.
                System.err.println("Something is terribly wrong, no revisionable found for revision");
                return null;
            }
            if(revisionable.getLatestRevisionNo() != entity.getKey().getRevisionNo()) {
                // Only latest revision can be a draft, something is wrong
                System.err.println("Something is wrong, only latest revision should be a draft and yet revisionable has something else as latest revision");
                return null;
            }
            entity.setState(RevisionState.APPROVED);
            revisionRepository.update(entity);
            revisionable.setCurApprovedNo(entity.getKey().getRevisionNo());
            revisionableRepository.update(revisionable);
        }

        return entity;
    }
}
