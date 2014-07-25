package fi.uta.fsd.metka.storage.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "SAVED_EXPERT_SEARCH")
public class SavedExpertSearchEntity {
    @Id
    @SequenceGenerator(name="SAVED_EXPERT_SEARCH_ID_SEQ", sequenceName="SAVED_EXPERT_SEARCH_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAVED_EXPERT_SEARCH_ID_SEQ")
    @Column(name = "SAVED_EXPERT_SEARCH_ID", updatable = false)
    private Long id;

    @Column(name = "QUERY")
    private String query;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SAVED_BY")
    private String savedBy;

    @Column(name = "SAVED_AT")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate savedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public LocalDate getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDate savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedExpertSearchEntity that = (SavedExpertSearchEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
