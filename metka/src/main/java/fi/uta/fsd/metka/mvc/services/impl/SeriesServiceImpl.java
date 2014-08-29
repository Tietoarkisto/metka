package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.mvc.services.SeriesService;
import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metka.transfer.revision.SeriesAbbreviationsResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesServiceImpl implements SeriesService {
    @Autowired
    private SeriesSearch search;

    @Override public SeriesAbbreviationsResponse findAbbreviations() {
        SeriesAbbreviationsResponse response = new SeriesAbbreviationsResponse();
        List<String> list = search.findAbbreviations();
        response.setResult(list.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.SEARCH_SUCCESS);
        response.getAbbreviations().add(""); // Let's add an empty value as the first value
        for(String string : list) {
            response.getAbbreviations().add(string);
        }
        return response;
    }

    @Override public RevisionSearchResponse findNames() {
        RevisionSearchResponse response = new RevisionSearchResponse();
        List<RevisionSearchResult> results = search.findNames();
        response.setResult(results.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.SEARCH_SUCCESS);
        for(RevisionSearchResult result : results) {
            response.getRows().add(result);
        }
        return response;
    }
}
