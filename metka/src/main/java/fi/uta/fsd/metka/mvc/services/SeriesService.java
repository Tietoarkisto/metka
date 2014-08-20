package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.SeriesAbbreviationsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService {
    @Autowired
    private SeriesSearch search;

    public SeriesAbbreviationsResponse findAbbreviations() {
        SeriesAbbreviationsResponse response = new SeriesAbbreviationsResponse();
        List<String> list = search.findAbbreviations();
        response.setResult(ReturnResult.SEARCH_SUCCESS);
        response.getAbbreviations().add(""); // Let's add an empty value as the first value
        for(String string : list) {
            response.getAbbreviations().add(string);
        }
        return response;
    }
}
