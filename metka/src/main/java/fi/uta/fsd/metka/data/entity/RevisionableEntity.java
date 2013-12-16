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
@Table(name = "VERSIONABLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 30)
public abstract class RevisionableEntity {
    @Id
    @SequenceGenerator(name="VERSIONABLE_ID_SEQ", sequenceName="VERSIONABLE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VERSIONABLE_ID_SEQ")
    @Column(name = "VERSIONABLE_ID", updatable = false)
    private Integer id;

    @Column(name = "TYPE", insertable=false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RevisionableType type;

    @Column(name = "CUR_APPROVED_ID")
    private Integer curApprovedId;

    @Column(name = "LATEST_REVISION_ID")
    private Integer latestRevisionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RevisionableType getType() {
        return type;
    }

    public void setType(RevisionableType type) {
        this.type = type;
    }

    public Integer getCurApprovedId() {
        return curApprovedId;
    }

    public void setCurApprovedId(Integer curApprovedId) {
        this.curApprovedId = curApprovedId;
    }

    public Integer getLatestRevisionId() {
        return latestRevisionId;
    }

    public void setLatestRevisionId(Integer latestRevisionId) {
        this.latestRevisionId = latestRevisionId;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
