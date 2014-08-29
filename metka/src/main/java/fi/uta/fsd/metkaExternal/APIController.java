package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.APIRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "")
public class APIController {
    @Autowired
    private APIRepository api;

    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private IndexerComponent indexer;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @RequestMapping(value = "createStudy", method = RequestMethod.POST)
    public @ResponseBody
    APIStudyCreateResponse createStudy(@RequestBody APIStudyCreateRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_STUDY_CREATE)) {
            APIStudyCreateResponse result = new APIStudyCreateResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return result;
        }

        RevisionCreateRequest revReq = new RevisionCreateRequest();
        revReq.setType(request.getType());
        revReq.getParameters().putAll(request.getParameters());
        Pair<ReturnResult, RevisionData> result = create.create(revReq);
        if(result.getLeft() != ReturnResult.REVISION_CREATED) {
            return new APIStudyCreateResponse(result.getLeft(), null);
        }

        Pair<StatusCode, ValueDataField> pair = result.getRight().dataField(ValueDataFieldCall.get("studyid"));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
            return new APIStudyCreateResponse(ReturnResult.PARAMETERS_MISSING, null);
        }

        return new APIStudyCreateResponse(ReturnResult.REVISION_CREATED, pair.getRight().getActualValueFor(Language.DEFAULT));
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public @ResponseBody
    RevisionSearchResponse performSearch(@RequestBody APISearchRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_SEARCH)) {
            RevisionSearchResponse response = new RevisionSearchResponse();
            response.setResult(ReturnResult.API_AUTHENTICATION_FAILED);
            return response;
        }
        try {
            ExpertRevisionSearchCommand command = ExpertRevisionSearchCommand.build(request.getQuery(), configurations);
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            RevisionSearchResponse response = new RevisionSearchResponse();
            if(results.getResults().isEmpty()) {
                response.setResult(ReturnResult.NO_RESULTS);
                return response;
            }
            response.setResult(ReturnResult.SEARCH_SUCCESS);
            for(RevisionResult revResult : results.getResults()) {
                Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(revResult.getId());
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                    // No revisionable so no actual data, continue
                    continue;
                }
                Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(revResult.getId(), revResult.getNo().intValue());
                if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    // No actual revision so no data, continue
                    continue;
                }
                RevisionSearchResult result = RevisionSearchResult.build(dataPair.getRight(), infoPair.getRight());
                response.getRows().add(result);
            }
            return response;
        } catch(QueryNodeException qne) {
            RevisionSearchResponse response = new RevisionSearchResponse();
            response.setResult(ReturnResult.MALFORMED_QUERY);
            return response;
        }
    }

    @RequestMapping(value = "getConfiguration", method = RequestMethod.POST)
    public @ResponseBody
    APIConfigurationReadResponse getConfiguration(@RequestBody APIConfigurationReadRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_READ)) {
            APIConfigurationReadResponse response = new APIConfigurationReadResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return response;
        }
        Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(request.getKey());
        return new APIConfigurationReadResponse(pair.getLeft(), pair.getRight());
    }

    @RequestMapping(value = "getRevision", method = RequestMethod.GET)
    public @ResponseBody
    APIRevisionReadResponse getData(@RequestBody APIRevisionReadRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_READ)) {
            APIRevisionReadResponse response = new APIRevisionReadResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return response;
        }
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(RevisionKey.fromModelKey(request.getKey()));
        APIRevisionReadResponse response = new APIRevisionReadResponse(dataPair.getLeft(), dataPair.getRight());
        return response;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public @ResponseBody ReturnResult saveData(@RequestBody APIRevisionSaveRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_EDIT)) {
            return ReturnResult.API_AUTHENTICATION_FAILED;
        }
        return revisions.updateRevisionData(request.getRevision());
    }

    @RequestMapping(value = "index", method = RequestMethod.POST)
    public @ResponseBody ReturnResult indexRevisions(@RequestBody APIMassIndexRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_SEARCH)) {
            return ReturnResult.API_AUTHENTICATION_FAILED;
        }
        for(Language language : request.getTargets().keySet()) {
            if(request.getTargets().get(language) == null || request.getTargets().get(language).isEmpty()) {
                continue;
            }
            for(IndexTarget target : request.getTargets().get(language)) {
                RevisionIndexerCommand command = RevisionIndexerCommand.index(target.getType(), language, target.getKey());
                indexer.addCommand(command);
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
