package fi.uta.fsd.metka.transfer.revision;

public class RevisionCompareRequest {
    private final Long id;
    private final Integer begin;
    private final Integer end;

    public RevisionCompareRequest(Long id, Integer begin, Integer end) {
        this.id = id;
        this.begin = begin;
        this.end = end;
    }

    public Long getId() {
        return id;
    }

    public Integer getBegin() {
        return begin;
    }

    public Integer getEnd() {
        return end;
    }
}
