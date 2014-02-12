package fi.uta.fsd.metka.data.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/10/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
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

    @Column(name = "CUR_APPROVED_NO")
    private Integer curApprovedNo;

    @Column(name = "LATEST_REVISION_NO")
    private Integer latestRevisionNo;

    @Column(name = "REMOVED")
    private Boolean removed = false;

    @Column(name = "REMOVAL_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate removalDate;

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
