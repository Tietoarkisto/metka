package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Identifies a single Revision from a combination of id (id of the revisionable object) and revision (ordering
 * number for revisions within revisionable).
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RevisionKey implements Comparable<RevisionKey> {
    @XmlElement private final Long id;
    @XmlElement private final Integer no;

    @JsonCreator
    public RevisionKey(@JsonProperty("id")Long id, @JsonProperty("no")Integer no) {
        this.id = id;
        this.no = no;
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionKey that = (RevisionKey) o;

        if (!id.equals(that.id)) return false;
        if (!no.equals(that.no)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + no.hashCode();
        return result;
    }

    @Override
    public int compareTo(RevisionKey o) {
        int result = id.compareTo(o.id);
        if(result == 0) {
            return no.compareTo(o.no);
        } else return result;
    }

    @Override
    public String toString() {
        return "JsonKey[name="+this.getClass().getSimpleName()+", keys={id: "+id+", no: "+no+"}]";
    }
}
