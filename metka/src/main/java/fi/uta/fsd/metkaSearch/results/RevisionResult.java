package fi.uta.fsd.metkaSearch.results;

import fi.uta.fsd.metka.enums.Language;

public class RevisionResult implements SearchResult {
    private final ResultList.ResultType type = ResultList.ResultType.REVISION;
    private final Long id;
    private final Long no;
    private final Language language;

    public RevisionResult(Long id, Long no, Language language) {
        this.id = id;
        this.no = no;
        this.language = language;
    }

    @Override
    public ResultList.ResultType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public Long getNo() {
        return no;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Result is "+"ID: "+id+" | NO: "+no;
    }
}
