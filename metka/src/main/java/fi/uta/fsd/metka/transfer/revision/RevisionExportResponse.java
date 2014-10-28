package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class RevisionExportResponse {
    private ReturnResult result;
    private String content;
    private Long id;
    private Integer no;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }
}
