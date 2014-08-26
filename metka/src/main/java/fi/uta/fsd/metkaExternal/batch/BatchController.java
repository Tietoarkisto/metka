package fi.uta.fsd.metkaExternal.batch;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: When external interfaces are truly possible this should be in a separate context from actual metka application (e.g. external)
@Controller("batch")
public class BatchController {
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private IndexerComponent indexer;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private GeneralRepository general;

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public @ResponseBody RevisionSearchResponse performSearch(@RequestBody String search) {
        try {
            ExpertRevisionSearchCommand command = ExpertRevisionSearchCommand.build(search, configurations);
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            RevisionSearchResponse response = new RevisionSearchResponse();
            if(results.getResults().isEmpty()) {
                response.setResult(ReturnResult.NO_RESULTS);
                return response;
            }
            response.setResult(ReturnResult.SEARCH_SUCCESS);
            for(RevisionResult revResult : results.getResults()) {
                Pair<ReturnResult, RevisionableInfo> infoPair = general.getRevisionableInfo(revResult.getId());
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                    // No revisionable so no actual data, continue
                    continue;
                }
                Pair<ReturnResult, RevisionData> dataPair = general.getRevisionData(revResult.getId(), revResult.getNo().intValue());
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

    @RequestMapping(value = "configuration", method = RequestMethod.POST)
    public @ResponseBody GetConfigurationResponse getConfiguration(@RequestBody ConfigurationKey key) {
        Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(key);
        return new GetConfigurationResponse(pair.getLeft(), pair.getRight());
    }

    @RequestMapping(value = "get", method = RequestMethod.POST)
    public @ResponseBody GetRequestResponse getData(@RequestBody RevisionKey key) {
        Pair<ReturnResult, RevisionData> dataPair = general.getRevisionData(key.getId(), key.getNo());
        GetRequestResponse response = new GetRequestResponse(dataPair.getLeft(), dataPair.getRight());
        return response;
    }

    public @ResponseBody ReturnResult saveData(@RequestBody RevisionData data) {
        return general.updateRevisionData(data);
    }

    public @ResponseBody ReturnResult indexRevisions(@RequestBody MassIndexRequest request) {
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
