package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.RevisionDataKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 8:59 AM
 */
@Entity
@Table(name = "REVISION_DATA")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class RevisionDataEntity {
    @EmbeddedId
    private RevisionDataKey key;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE", updatable = false)
    private String state;

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_MODIFIED", updatable = false)
    private Date lastModified;

    @ManyToOne
    @JoinColumn(name = "MODIFIED_BY", updatable = false)
    private PersonEntity modifiedBy;

    @ManyToOne
    @JoinColumn(name = "HANDLER", updatable = false)
    private PersonEntity handler;

    public RevisionDataKey getKey() {
        return key;
    }

    public void setKey(RevisionDataKey key) {
        this.key = key;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public PersonEntity getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(PersonEntity modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public PersonEntity getHandler() {
        return handler;
    }

    public void setHandler(PersonEntity handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    public static enum DataState {
        DRAFT,
        PUBLISHED,
        REMOVED,
        REVISION
    }
}
