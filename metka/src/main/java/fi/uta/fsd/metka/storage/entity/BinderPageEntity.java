package fi.uta.fsd.metka.storage.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "BINDER_PAGE")
public class BinderPageEntity {
    @Id
    @SequenceGenerator(name="PAGE_ID_SEQ", sequenceName="PAGE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PAGE_ID_SEQ")
    @Column(name = "PAGE_ID", updatable = false, insertable = false, unique = true)
    private Long pageId;

    @Column(name = "BINDER_ID", updatable = false)
    private Long binderId;

    @Column(name = "STUDY", updatable = false)
    private Long study;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SAVED_AT")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime savedAt;

    @Column(name = "SAVED_BY")
    private String savedBy;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getBinderId() {
        return binderId;
    }

    public void setBinderId(Long binderId) {
        this.binderId = binderId;
    }

    public Long getStudy() {
        return study;
    }

    public void setStudy(Long study) {
        this.study = study;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }
}
