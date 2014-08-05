package fi.uta.fsd.metka.mvc.search;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchRequest;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface SeriesSearch {
    public List<String> findAbbreviations();

    /**
     * Return all series matching the given search terms.
     * @param query User defined search terms for finding series
     * @return List of Object[2]. First column contains the RevisionData object second column contains the removed value of the series.
     */
    public Pair<ReturnResult, List<RevisionSearchResult>> findSeries(RevisionSearchRequest query);
}
