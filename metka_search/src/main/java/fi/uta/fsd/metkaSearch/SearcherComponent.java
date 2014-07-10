package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import org.apache.lucene.index.IndexReader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SearcherComponent {

    private final Map<DirectoryManager.DirectoryPath, IndexReader> readers = new ConcurrentHashMap<>();

    // TODO: Something like this is needed to execute searches to index from the UI side
    /*public SearchResult executeSearch(MetkaSearch search) {

    }*/
}
