package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "revisionData")
public class RevisionData {
    @XmlElement private final RevisionKey key;
    @XmlElement private final ConfigurationKey configuration;
    @XmlElement private Map<String, Change> changes = new HashMap<String, Change>();
    @XmlElement private Map<String, FieldContainer> fields = new HashMap<String, FieldContainer>();
    @XmlElement private RevisionState state;

    @JsonCreator
    public RevisionData(@JsonProperty("key")RevisionKey key, @JsonProperty("configuration")ConfigurationKey configuration) {
        this.key = key;
        this.configuration = configuration;
    }

    public RevisionKey getKey() {
        return key;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    public Map<String, FieldContainer> getFields() {
        return fields;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionData that = (RevisionData) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
