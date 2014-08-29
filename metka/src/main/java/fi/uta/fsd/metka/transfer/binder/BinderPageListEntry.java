package fi.uta.fsd.metka.transfer.binder;

import fi.uta.fsd.metka.model.general.DateTimeUserPair;

public class BinderPageListEntry {
    private Long pageId;
    private Long study;
    private String studyId;
    private String studyTitle;
    private DateTimeUserPair saved;
    private Long binderId;
    private String description;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getStudy() {
        return study;
    }

    public void setStudy(Long study) {
        this.study = study;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public Long getBinderId() {
        return binderId;
    }

    public void setBinderId(Long binderId) {
        this.binderId = binderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
