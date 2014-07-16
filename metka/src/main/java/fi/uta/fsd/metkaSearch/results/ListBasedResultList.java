package fi.uta.fsd.metkaSearch.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListBasedResultList<T extends SearchResult> implements ResultList<T> {
    private final ResultType type;
    private final List<T> results = new ArrayList<>();

    public ListBasedResultList(ResultType type) {
        this.type = type;
    }

    @Override
    public ResultType getType() {
        return type;
    }

    @Override
    public boolean addResult(T result) {
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
    public List<T> getResults() {
        return new ArrayList<>(results);
    }

    @Override
    public void sort(Comparator<T> comparator) {
        Collections.sort(results, comparator);
    }
}
