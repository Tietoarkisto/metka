package fi.uta.fsd.metkaSearch.results;

import java.util.Comparator;
import java.util.List;

/**
 * Common interface for all search results in Metka software.
 * Doesn't really define that much functionality since there's a great deal of variation between
 * the actual data contained in different result lists.
 *
 * TODO: Possibly extend List so Result Lists can be iterated over but more likely provide a wrapper for contained list
 */
public interface ResultList<T extends SearchResult> {
    public static enum ResultType {
        BOOLEAN, // Simple true|false result. Most often used to check restrictions for validity
        REVISION // Just a plain old revision result, should contain revision key and nothing else
        // ... Add more as needed. It might be useful to have the Search handler fetch some predefined data from the revision in some cases
    }

    /**
     * Provide the type of this Result Set.
     * This should be set internally by different set constructors.
     * @return
     */
    public ResultType getType();

    /**
     * Adds search result to some form of internal container.
     * Most ResultLists will check that the type of SearchResult is the same as the type of ResultList before adding
     * @param result Search Result to be added to ResultList
     * @return Was adding successful. Most common failure is trying to add a SearchResult with type that differs from ResultLists type.
     */
    public boolean addResult(T result);

    /**
     * Returns results in a list ready to be iterated over.
     * It doesn't matter how the implementation collects the results, they should always be
     * returned in an ordered list since they are always displayed that way.
     * Client can do reordering later but that is not the problem of the search system.
     * @return
     */
    public List<T> getResults();

    /**
     * Sorts the contained list using given comparator.
     * @param comparator
     */
    public void sort(Comparator<T> comparator);
}
