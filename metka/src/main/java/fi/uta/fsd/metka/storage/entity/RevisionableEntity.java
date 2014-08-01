package fi.uta.fsd.metka.storage.entity;

import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.enums.RevisionState;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "REVISIONABLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 30)
public abstract class RevisionableEntity {
    @Id
    @SequenceGenerator(name="REVISIONABLE_ID_SEQ", sequenceName="REVISIONABLE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REVISIONABLE_ID_SEQ")
    @Column(name = "REVISIONABLE_ID", updatable = false)
    private Long id;

    @Column(name = "TYPE", insertable=false, updatable = false)
    private String type;

    @Column(name = "CUR_APPROVED_NO")
    private Integer curApprovedNo;

    @Column(name = "LATEST_REVISION_NO")
    private Integer latestRevisionNo;

    @Column(name = "REMOVED")
    private Boolean removed = false;

    @Column(name = "REMOVAL_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime removalDate;

    @Column(name = "REMOVED_BY")
    private String removedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCurApprovedNo() {
        return curApprovedNo;
    }

    public void setCurApprovedNo(Integer curApprovedNo) {
        this.curApprovedNo = curApprovedNo;
    }

    public Integer getLatestRevisionNo() {
        return latestRevisionNo;
    }

    public void setLatestRevisionNo(Integer latestRevisionNo) {
        this.latestRevisionNo = latestRevisionNo;
    }

    public Boolean getRemoved() {
        return (removed == null) ? false : removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null) ? false : removed;
    }

    public LocalDateTime getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(LocalDateTime removalDate) {
        this.removalDate = removalDate;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }

    // Helper for creating new revisions
    public RevisionEntity createNextRevision() {
        RevisionEntity revision;
        if(latestRevisionNo == null) {
            revision = new RevisionEntity(new RevisionKey(getId(), 1));
        } else {
            revision = new RevisionEntity(new RevisionKey(getId(), latestRevisionNo+1));
        }
        revision.setState(RevisionState.DRAFT);
        return revision;
    }

    public RevisionKey latestRevisionKey() {
        return new RevisionKey(getId(), getLatestRevisionNo());
    }

    public RevisionKey currentApprovedRevisionKey() {
        return new RevisionKey(getId(), getCurApprovedNo());
    }

    /**
     * Simple check to see if this revisionable has an open DRAFT.
     * Assuming that everything else works as it should then there's an open draft if and only if currentApprovedNo is null
     * or latestRevisionNo is larger than currentApprovedNo.
     * Additional check should be made to make sure that the revision actually is what it should be but if it's not
     * then it's a case of revision being out of sync with revisionable.
     * @return True if there should be a draft, false otherwise
     */
    public boolean hasDraft() {
        if(curApprovedNo == null) {
            return true;
        }
        if(latestRevisionNo > curApprovedNo) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionableEntity that = (RevisionableEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
