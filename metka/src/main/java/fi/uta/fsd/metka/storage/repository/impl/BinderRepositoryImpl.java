package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.BinderPageEntity;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderPageListEntry;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BinderRepositoryImpl implements BinderRepository {
    private static Logger logger = LoggerFactory.getLogger(BinderRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private StudySearch studies;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, BinderPageListEntry> saveBinderPage(SaveBinderPageRequest request) {
        Pair<ReturnResult, RevisionData> pair = studies.getLatestRevisionWithStudyId(request.getStudyId());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }
        RevisionData study = pair.getRight();

        ReturnResult result = null;
        BinderPageEntity page = null;
        if(request.getPageId() != null) {
            page = em.find(BinderPageEntity.class, request.getPageId());
        }
        if(page == null) {
            page = new BinderPageEntity();
            page.setBinderId(request.getBinderId());
            page.setStudy(study.getKey().getId());
            em.persist(page);
            result = ReturnResult.PAGE_CREATED;
        }
        page.setDescription(request.getDescription());
        page.setSavedAt(new LocalDateTime());
        page.setSavedBy(AuthenticationUtil.getUserName());

        BinderPageListEntry entry = formPageListEntry(study, page);

        return new ImmutablePair<>(result == null ? ReturnResult.PAGE_UPDATED : result, entry);
    }

    @Override
    public ReturnResult removePage(Long pageId) {
        em.remove(em.find(BinderPageEntity.class, pageId));
        return ReturnResult.PAGE_REMOVED;
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> listStudyBinderPages(Long study) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.study=:study ORDER BY p.pageId ASC", BinderPageEntity.class)
                .setParameter("study", study)
                .getResultList();
        List<BinderPageListEntry> pages = new ArrayList<>();
        for(BinderPageEntity entity : entities) {
            BinderPageListEntry page = new BinderPageListEntry();
            page.setPageId(entity.getPageId());
            page.setBinderId(entity.getBinderId());
            page.setDescription(entity.getDescription());
            pages.add(page);
        }
        return new ImmutablePair<>(pages.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.SEARCH_SUCCESS, pages);
    }

    @Override
    public void removeStudyBinderPages(Long study) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.study=:study ORDER BY p.pageId ASC", BinderPageEntity.class)
                .setParameter("study", study)
                .getResultList();
        for(BinderPageEntity entity : entities) {
            em.remove(entity);
        }
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> listBinderPages() {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p ORDER BY p.binderId ASC", BinderPageEntity.class).getResultList();
        return formPageList(entities);
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> binderContent(Long binderId) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.binderId=:binderId ORDER BY p.study ASC", BinderPageEntity.class)
                .setParameter("binderId", binderId)
                .getResultList();
        return formPageList(entities);
    }

    private Pair<ReturnResult, List<BinderPageListEntry>> formPageList(List<BinderPageEntity> entities) {
        List<BinderPageListEntry> pages = new ArrayList<>();
        for(BinderPageEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(entity.getStudy(), false, ConfigurationType.STUDY);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            pages.add(formPageListEntry(pair.getRight(), entity));
        }
        return new ImmutablePair<>(pages.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.SEARCH_SUCCESS, pages);
    }

    private BinderPageListEntry formPageListEntry(RevisionData study, BinderPageEntity page) {
        BinderPageListEntry entry = new BinderPageListEntry();
        entry.setPageId(page.getPageId());
        entry.setBinderId(page.getBinderId());
        entry.setStudy(page.getStudy());
        Pair<StatusCode, ValueDataField> field = study.dataField(ValueDataFieldCall.get("studyid"));
        entry.setStudyId(field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));
        entry.setDescription(page.getDescription());
        entry.setSaved(new DateTimeUserPair(page.getSavedAt(), page.getSavedBy()));
        field = study.dataField(ValueDataFieldCall.get("title"));
        entry.setStudyTitle(field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));
        return entry;
    }
}
