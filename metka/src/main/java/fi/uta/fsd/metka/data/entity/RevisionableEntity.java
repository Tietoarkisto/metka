package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.RevisionableType;

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

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "REVISIONABLE_ID", referencedColumnName = "REVISIONABLE_ID", insertable = false, updatable = false),
            @JoinColumn(name = "CUR_APPROVED_NO", referencedColumnName = "REVISION_NO", insertable = false, updatable = false)
    })
    private RevisionEntity curApprovedRev;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "REVISIONABLE_ID", referencedColumnName = "REVISIONABLE_ID", insertable = false, updatable = false),
            @JoinColumn(name = "LATEST_REVISION_NO", referencedColumnName = "REVISION_NO", insertable = false, updatable = false)
    })
    private RevisionEntity latestRevision;

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

    public RevisionEntity getCurApprovedRev() {
        return curApprovedRev;
    }

    public RevisionEntity getLatestRevision() {
        return latestRevision;
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
