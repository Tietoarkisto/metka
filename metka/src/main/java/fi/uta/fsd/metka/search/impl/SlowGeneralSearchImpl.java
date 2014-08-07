package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.search.GeneralSearch;
import fi.uta.fsd.metka.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository("generalSearch")
public class SlowGeneralSearchImpl implements GeneralSearch {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private GeneralRepository general;

    @Override
    public List<RevisionDataRemovedContainer> tempFindAllStudies() {
        List<RevisionDataRemovedContainer> result = new ArrayList<>();
        List<StudyEntity> entities = em.createQuery("SELECT s FROM StudyEntity s ORDER BY s.id ASC", StudyEntity.class)
                .getResultList();
        RevisionDataRemovedContainer container;
        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(entity.getCurApprovedNo() != null) {
                    Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(entity.currentApprovedRevisionKey(), ConfigurationType.STUDY);

                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        container = new RevisionDataRemovedContainer(pair.getRight(), false);
                        result.add(container);
                    }
                }
                if(entity.hasDraft()) {
                    Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.STUDY);

                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        container = new RevisionDataRemovedContainer(pair.getRight(), false);
                        result.add(container);
                    }
                }
            } else {
                Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(entity.currentApprovedRevisionKey(), ConfigurationType.STUDY);

                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    container = new RevisionDataRemovedContainer(pair.getRight(), true);
                    result.add(container);
                }
            }
        }
        return result;
    }

}
