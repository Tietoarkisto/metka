package fi.uta.fsd.metkaSearch.results;

import java.util.ArrayList;
import java.util.List;

public class ListBasedResultList implements ResultList {
    private final ResultType type;
    private final List<SearchResult> results = new ArrayList<>();

    public ListBasedResultList(ResultType type) {
        this.type = type;
    }

    @Override
    public ResultType getType() {
        return type;
    }

    @Override
    public boolean addResult(SearchResult result) {
        if(result.getType() != type) {
            return false;
        }
        results.add(result);

        return true;
    }

    /**
     * Return a clone of the results-list for immutability.
     * @return
     */
    @Override
    public List<SearchResult> getResults() {
        return new ArrayList<>(results);
    }
}
