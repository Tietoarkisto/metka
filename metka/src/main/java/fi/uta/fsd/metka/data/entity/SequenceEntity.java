package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;

@Table(name="SEQUENCE_HOLDER")
@Entity
public class SequenceEntity {
    @Id
    @Column(name = "SEQUENCE_ID", updatable = false, nullable = false)
    private String key;

    @Column(name = "SEQUENCE")
    private Long sequence;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceEntity that = (SequenceEntity) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
