package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
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

    public StudyEntity createStudy() {
        return studyRepository.create(new StudyEntity());
    }

    public List<StudyEntity> listAllStudies() {
        return studyRepository.listAll();
    }

    public RevisionEntity addDraft(RevisionableEntity revisionable) {
        if(revisionable.getLatestRevisionId() != revisionable.getCurApprovedId()) {
            return revisionRepository.read((new RevisionKey(revisionable.getId(), revisionable.getLatestRevisionId())));
        } else {
            RevisionEntity revision = new RevisionEntity(new RevisionKey(revisionable.getId(), revisionable.getLatestRevisionId()+1));
            revisionRepository.create(revision);
            revisionable.setLatestRevisionId(revision.getKey().getRevisionNo());
            revisionableRepository.update(revisionable);
            return revision;
        }
    }
}
