package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.RevisionState;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/11/13
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "REVISION")
public class RevisionEntity {
    @EmbeddedId
    private RevisionKey key;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private RevisionState state;

    @Lob
    @Column(name = "DATA", length = 10000)
    // Length defined because HSQL has problems for some reason, should be removed.
    private String data;

    public RevisionEntity() {
    }

    public RevisionEntity(RevisionKey key) {
        this.key = key;
    }

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
