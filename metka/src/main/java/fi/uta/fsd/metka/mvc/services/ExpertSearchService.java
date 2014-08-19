package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.expert.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertSearchService {
    private static Logger logger = LoggerFactory.getLogger(ExpertSearchService.class);
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private SavedSearchRepository savedSearch;

    public ExpertSearchQueryResponse performQuery(ExpertSearchQueryRequest request) {
        ExpertSearchQueryResponse response = new ExpertSearchQueryResponse();
        response.setOperation(ExpertSearchOperation.QUERY);
        SearchCommand<RevisionResult> command = null;
        try {
            command = ExpertRevisionSearchCommand.build(request.getQuery(), configurations);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        List<RevisionResult> results = searcher.executeSearch(command).getResults();

        for(RevisionResult result : results) {
            Pair<ReturnResult, RevisionableInfo> infoPair = general.getRevisionableInfo(result.getId());
            if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                logger.warn("Revisionable was not found for id "+result.getId());
                continue;
            }
            Pair<ReturnResult, RevisionData> pair = general.getRevisionData(result.getId(), result.getNo().intValue());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.warn("Couldn't find a revision for search result "+result.toString());
                continue;
            }
            RevisionableInfo info = infoPair.getRight();
            RevisionData revision = pair.getRight();
            ExpertSearchRevisionQueryResult qr = new ExpertSearchRevisionQueryResult();
            if(info.getRemoved()) {
                qr.setState(UIRevisionState.REMOVED);
            } else {
                qr.setState(UIRevisionState.fromRevisionState(revision.getState()));
            }
            qr.setId(revision.getKey().getId());
            qr.setNo(revision.getKey().getNo());
            qr.setType(revision.getConfiguration().getType());
            // TODO: Maybe generalize this better
            switch(revision.getConfiguration().getType()) {
                case STUDY: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get("title"));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(command.getPath().getLanguage()));
                    }
                    break;
                }
                case SERIES: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get("seriesname"));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(command.getPath().getLanguage()));
                    }
                    break;
                }
            }
            response.getResults().add(qr);
        }
        return response;
    }

    public ExpertSearchListResponse listSavedSearcher() {
        ExpertSearchListResponse response = new ExpertSearchListResponse();
        List<SavedExpertSearchItem> items = savedSearch.listSavedSearches();
        for(SavedExpertSearchItem item : items) {
            response.getQueries().add(item);
        }
        return response;
    }

    public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem item) {
        return savedSearch.saveExpertSearch(item);
    }

    public void removeExpertSearch(Long id) {
        savedSearch.removeExpertSearch(id);
    }
}
