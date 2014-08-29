package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {
    private static Logger logger = LoggerFactory.getLogger(SlowSeriesSearchImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public List<String> findAbbreviations() {
        List<String> list = new ArrayList<>();

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for series "+entity.toString());
                continue;
            }
            RevisionData revision = pair.getRight();
            // Use the method with less sanity checks since there's no point in getting configuration here.
            Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("seriesabbr"));
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                list.add(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List<RevisionSearchResult> findNames() {
        List<RevisionSearchResult> results = new ArrayList<>();

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for series "+entity.toString());
                continue;
            }
            RevisionSearchResult result = new RevisionSearchResult();
            RevisionData revision = pair.getRight();
            result.setId(revision.getKey().getId());
            Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("seriesname"));
            result.getValues().put("seriesname",
                    fieldPair.getLeft() == StatusCode.FIELD_FOUND
                            ? fieldPair.getRight().getActualValueFor(Language.DEFAULT)
                            : "");
            results.add(result);
        }
        Collections.sort(results, new Comparator<RevisionSearchResult>() {
            @Override
            public int compare(RevisionSearchResult o1, RevisionSearchResult o2) {
                int result = o1.getValues().get("seriesname").compareTo(o2.getValues().get("seriesname"));
                return result != 0 ? result : o1.getId().compareTo(o2.getId());
            }
        });
        return results;
    }


}
