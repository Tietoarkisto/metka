package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.expert.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ExpertSearchServiceImpl implements ExpertSearchService {
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private SavedSearchRepository savedSearch;

    @Override public ExpertSearchQueryResponse performQuery(ExpertSearchQueryRequest request) throws Exception {
        ExpertSearchQueryResponse response = new ExpertSearchQueryResponse();
        response.setOperation(ExpertSearchOperation.QUERY);
        if(!StringUtils.hasText(request.getQuery())) {
            response.setResult(ReturnResult.EMPTY_QUERY);
            return response;
        }
        SearchCommand<RevisionResult> command = null;
        try {
            command = ExpertRevisionSearchCommand.build(request.getQuery(), configurations);
        } catch(QueryNodeException e) {
            Logger.error(ExpertSearchServiceImpl.class, "Exception while forming search command.", e);
            switch(e.getMessageObject().getKey()) {
                case "EMPTY_QUERY":
                    response.setResult(ReturnResult.EMPTY_QUERY);
                    break;
                case "MALFORMED_LANGUAGE":
                    response.setResult(ReturnResult.MALFORMED_LANGUAGE);
                    break;
                case "INVALID_SYNTAX_CANNOT_PARSE":
                    response.setResult(ReturnResult.MALFORMED_QUERY);
                    break;
            }
            return response;
        } catch(Exception e) {
            Logger.error(ExpertSearchServiceImpl.class, "Exception while forming search command.", e);
            throw e;
        }
        List<RevisionResult> results = searcher.executeSearch(command).getResults();

        for(RevisionResult result : results) {
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(result.getId());
            if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                Logger.warning(ExpertSearchServiceImpl.class, "Revisionable was not found for id "+result.getId());
                continue;
            }
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(result.getId(), result.getNo().intValue());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.warning(ExpertSearchServiceImpl.class, "Couldn't find a revision for search result "+result.toString());
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
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(command.getPath().getLanguage()));
                    }
                    break;
                }
                case SERIES: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.SERIESNAME));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(command.getPath().getLanguage()));
                    }
                    break;
                }
                case PUBLICATION: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.PUBLICATIONTITLE));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(command.getPath().getLanguage()));
                    }
                    break;
                }
            }
            response.getResults().add(qr);
        }
        response.setResult(ReturnResult.OPERATION_SUCCESSFUL);
        return response;
    }

    @Override public ExpertSearchListResponse listSavedSearcher() {
        ExpertSearchListResponse response = new ExpertSearchListResponse();
        List<SavedExpertSearchItem> items = savedSearch.listSavedSearches();
        for(SavedExpertSearchItem item : items) {
            response.getQueries().add(item);
        }
        return response;
    }

    @Override public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem item) {
        return savedSearch.saveExpertSearch(item);
    }

    @Override public void removeExpertSearch(Long id) {
        savedSearch.removeExpertSearch(id);
    }
}
