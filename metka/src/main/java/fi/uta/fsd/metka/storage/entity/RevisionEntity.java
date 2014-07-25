package fi.uta.fsd.metka.storage.entity;

import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.enums.RevisionState;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "REVISION")
public class RevisionEntity {
    @EmbeddedId
    private RevisionKey key;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private RevisionState state;

    @Lob
    @Column(name = "DATA")
    @Type(type="org.hibernate.type.StringClobType")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionEntity that = (RevisionEntity) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
