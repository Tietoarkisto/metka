package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {
    private static Logger logger = LoggerFactory.getLogger(SlowSeriesSearchImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private GeneralRepository general;

    @Override
    public List<String> findAbbreviations() {
        List<String> list = new ArrayList<>();

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for series "+entity.toString());
                continue;
            }
            RevisionData revision = pair.getRight();
            // Use the method with less sanity checks since there's no point in getting configuration here.
            Pair<StatusCode, SavedDataField> fieldPair = revision.dataField(SavedDataFieldCall.get("seriesabbr"));
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValue()) {
                list.add(fieldPair.getRight().getActualValue());
            }
        }
        Collections.sort(list);
        return list;
    }


}
