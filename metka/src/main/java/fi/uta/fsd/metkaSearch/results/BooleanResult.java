package fi.uta.fsd.metkaSearch.results;

public class BooleanResult implements SearchResult {
    private final ResultList.ResultType type = ResultList.ResultType.BOOLEAN;
    private final boolean result;

    public BooleanResult(boolean result) {
        this.result = result;
    }

    @Override
    public ResultList.ResultType getType() {
        return type;
    }

    public boolean getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Result is: "+result;
    }
}
