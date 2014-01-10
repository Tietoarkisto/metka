package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RevisionKey {
    @XmlElement private final Integer id;
    @XmlElement private final Integer revision;

    @JsonCreator
    public RevisionKey(@JsonProperty("id")Integer id, @JsonProperty("revision")Integer revision) {
        this.id = id;
        this.revision = revision;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRevision() {
        return revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionKey that = (RevisionKey) o;

        if (!id.equals(that.id)) return false;
        if (!revision.equals(that.revision)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }
}
