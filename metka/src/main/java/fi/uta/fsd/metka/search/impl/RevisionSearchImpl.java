package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.stringToLong;

@Repository
public class RevisionSearchImpl implements RevisionSearch {
    private static Logger logger = LoggerFactory.getLogger(RevisionSearchImpl.class);

    @Autowired
    private GeneralRepository general;

    @Autowired
    private SearcherComponent searcher;

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> search(RevisionSearchRequest request) {
        switch(request.getType()) {
            case SERIES:
                return findSeries(request);
            case STUDY:
                return findStudies(request);
            case PUBLICATION:
                return findPublications(request);
            default:
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }
    }

    private Pair<ReturnResult, List<RevisionSearchResult>> findSeries(RevisionSearchRequest request) {
        // TODO: get path language from some common source that knows which language we should search.
        SeriesBasicSearchCommand command;
        try {
            command = SeriesBasicSearchCommand.build(Language.DEFAULT.toValue(), request.isSearchApproved(), request.isSearchDraft(), request.isSearchRemoved(),
                    stringToLong(request.getByKey("id")), request.getByKey("seriesabbr"), request.getByKey("seriesname"));
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            qne.printStackTrace();
            return new ImmutablePair<>(ReturnResult.SEARCH_FAILED, null);
        }
    }

    private Pair<ReturnResult, List<RevisionSearchResult>> findStudies(RevisionSearchRequest request) {
        // TODO: get path language from some common source that knows which language we should search.
        // TODO: Implement study basic search
        /*StudyBasicSearchCommand command;
        try {
            command = StudyBasicSearchCommand.build(Language.DEFAULT.toValue(), request.isSearchApproved(), request.isSearchDraft(), request.isSearchRemoved(),);
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            qne.printStackTrace();
            return new ImmutablePair<>(ReturnResult.SEARCH_FAILED, null);
        }*/
        return null;
    }

    private Pair<ReturnResult, List<RevisionSearchResult>> findPublications(RevisionSearchRequest request) {
        // TODO: get path language from some common source that knows which language we should search.
        // TODO: Implement publication basic search
        /*PublicationBasicSearchCommand command;
        try {
            command = PublicationBasicSearchCommand.build(Language.DEFAULT.toValue(), request.isSearchApproved(), request.isSearchDraft(), request.isSearchRemoved(),);
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            qne.printStackTrace();
            return new ImmutablePair<>(ReturnResult.SEARCH_FAILED, null);
        }
        */
        return null;
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

        Pair<ReturnResult, RevisionableInfo> infoPair = null;
        for(RevisionResult result : resultList.getResults()) {
            RevisionableInfo info = null;
            if(infoPair == null || !infoPair.getRight().getId().equals(result.getId())) {
                infoPair = general.getRevisionableInfo(result.getId());
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                    logger.error("Couldn't find info for revisionable "+result.getId());
                    continue;
                }
                info = infoPair.getRight();
            }
            // NOTICE: Try to remove the need to do this, although granted this isn't exactly heavy
            if(info == null || (info.getApproved() != null && result.getNo() < info.getApproved())) {
                continue;
            }

            Pair<ReturnResult, RevisionData> pair = general.getRevisionData(result.getId(), result.getNo().intValue());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Failed to find revision for id: "+result.getId()+" and no: "+result.getNo());
                continue;
            }
            RevisionData data = pair.getRight();
            RevisionSearchResult searchResult = new RevisionSearchResult();
            searchResult.setId(data.getKey().getId());
            searchResult.setNo(data.getKey().getNo());
            searchResult.setState((info.getRemoved()) ? UIRevisionState.REMOVED : UIRevisionState.fromRevisionState(data.getState()));
            // Add SERIES specific search result values
            Pair<StatusCode, SavedDataField> fieldPair = data.dataField(SavedDataFieldCall.get("seriesname"));
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValue()) {
                searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValue());
            } else {
                searchResult.getValues().put("seriesname", "");
            }
            results.add(searchResult);
        }

        return results;
    }
}
