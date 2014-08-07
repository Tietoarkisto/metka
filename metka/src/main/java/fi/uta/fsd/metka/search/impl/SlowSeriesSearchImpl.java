package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchRequest;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.series.SeriesBasicSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.stringToLong;

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
        list.add("");

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't found revision for series "+entity.toString());
                continue;
            }
            RevisionData revision = pair.getRight();
            // Use the method with less sanity checks since there's no point in getting configuration here.
            SavedDataField field = revision.dataField(SavedDataFieldCall.get("seriesabbr")).getRight();
            if(field != null && field.hasValue()) list.add(field.getActualValue());
        }
        Collections.sort(list);
        return list;
    }


}
