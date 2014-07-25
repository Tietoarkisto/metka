package fi.uta.fsd.metka.enums.repositoryResponses;

public class DraftRemoveResponse {
    private final Response response;
    private final Long id;
    private final Integer no;

    public DraftRemoveResponse(Response response, Long id, Integer no) {
        this.response = response;
        this.id = id;
        this.no = no;
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public Response getResponse() {
        return response;
    }
    public static enum Response {
        SUCCESS, NO_DRAFT, NO_REVISIONABLE, FINAL_REVISION
    }
}
