package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.RevisionKey;

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

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    public static enum RevisionState {
        DRAFT,
        PUBLISHED,
        REMOVED,
        REVISION
    }
}
