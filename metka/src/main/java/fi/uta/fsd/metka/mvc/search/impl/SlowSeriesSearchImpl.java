package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
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
            if(field != null && !StringUtils.isEmpty(field.getActualValue())) list.add(field.getActualValue());
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> findSeries(RevisionSearchRequest query) {
        // TODO: Make path management sensible, get path language from some common source that knows which language we should search.
        SeriesBasicSearchCommand command;
        try {
            command = SeriesBasicSearchCommand.build("fi", query.isSearchApproved(), query.isSearchDraft(), query.isSearchRemoved(),
                    stringToLong(query.getByKey("seriesno")), query.getByKey("seriesabbr"), query.getByKey("seriesname"));
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            qne.printStackTrace();
            return new ImmutablePair<>(ReturnResult.SEARCH_FAILED, null);
        }
    }

    private List<RevisionSearchResult> collectResults(ResultList<RevisionResult> resultList) {
        List<RevisionSearchResult> results = new ArrayList<>();

        resultList.sort(new Comparator<RevisionResult>() {
            @Override
            public int compare(RevisionResult o1, RevisionResult o2) {
                if(o1.getId().compareTo(o2.getId()) == 0) {
                    return o1.getNo().compareTo(o2.getNo());
                } else {
                    return o1.getId().compareTo(o2.getId());
                }
            }
        });

        if(resultList.getType() != ResultList.ResultType.REVISION) {
            // This only knows how to handle revision results
            return results;
        }
        RevisionableEntity entity = null;
        for(RevisionResult result : resultList.getResults()) {
            if(entity == null || !entity.getId().equals(result.getId())) {
                entity = em.find(RevisionableEntity.class, result.getId());
            }
            // NOTICE: Try to remove the need to do this, although granted this isn't exactly heavy
            if(entity.getCurApprovedNo() != null && result.getNo() < entity.getCurApprovedNo()) {
                continue;
            }

            RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(result.getId(), result.getNo().intValue()));
            if(revision != null && !StringUtils.isEmpty(revision.getData())) {
                Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(revision.getData());
                if(pair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                    logger.error("Failed to deserialize "+revision.toString());
                    continue;
                }
                RevisionData data = pair.getRight();
                RevisionSearchResult searchResult = new RevisionSearchResult();
                searchResult.setId(data.getKey().getId());
                searchResult.setNo(data.getKey().getNo());
                searchResult.setState((entity.getRemoved()) ? UIRevisionState.REMOVED : UIRevisionState.fromRevisionState(data.getState()));
                // Add SERIES specific search result values
                Pair<StatusCode, SavedDataField> fieldPair = data.dataField(SavedDataFieldCall.get("seriesname"));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValue()) {
                    searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValue());
                } else {
                    searchResult.getValues().put("seriesname", "");
                }
                results.add(searchResult);
            }
        }

        return results;
    }
}
