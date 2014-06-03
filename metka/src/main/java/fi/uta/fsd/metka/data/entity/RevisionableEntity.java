package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.RevisionState;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

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
    private Integer id;

    @Column(name = "TYPE", insertable=false, updatable = false)
    private String type;

    /*@EmbeddedId
    @AttributeOverride(name = "type", column = @Column(name = "TYPE", insertable = false, updatable = false))
    private RevisionableKey key;*/

    @Column(name = "CUR_APPROVED_NO")
    private Integer curApprovedNo;

    @Column(name = "LATEST_REVISION_NO")
    private Integer latestRevisionNo;

    @Column(name = "REMOVED")
    private Boolean removed = false;

    @Column(name = "REMOVAL_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate removalDate;

    /*public RevisionableKey getKey() {
        return key;
    }

    public void setKey(RevisionableKey key) {
        this.key = key;
    }*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public LocalDate getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(LocalDate removalDate) {
        this.removalDate = removalDate;
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

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionableEntity that = (RevisionableEntity) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }*/

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

    /*@Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }*/

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
