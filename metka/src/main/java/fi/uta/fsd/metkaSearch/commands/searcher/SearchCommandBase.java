package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.search.Query;

public abstract class SearchCommandBase<T extends SearchResult> implements SearchCommand<T> {
    private final DirectoryManager.DirectoryPath path;
    private final ResultList.ResultType resultType;


    protected SearchCommandBase(DirectoryManager.DirectoryPath path, ResultList.ResultType resultType) {
        this.path = path;
        this.resultType = resultType;
    }

    @Override
    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    @Override
    public ResultList.ResultType getResultType() {
        return resultType;
    }

    @Override
    public abstract Query getQuery();

    @Override
    public abstract ResultHandler<T> getResultHandler();
}
