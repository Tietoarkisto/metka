package fi.uta.fsd.metka.mvc.domain.model.data;

import fi.uta.fsd.metka.mvc.domain.model.configuration.ConfigurationKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    @XmlElement private RevisionKey key;
    @XmlElement private ConfigurationKey configuration;
    @XmlElement private Map<String, Change> changes = new HashMap<String, Change>();
    @XmlElement private Map<String, FieldContainer> fields = new HashMap<String, FieldContainer>();

    public RevisionData() {
    }

    public RevisionData(RevisionKey key) {
        this.key = key;
    }

    public RevisionData(RevisionKey key, ConfigurationKey configuration) {
        this.key = key;
        this.configuration = configuration;
    }

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationKey configuration) {
        this.configuration = configuration;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    public Map<String, FieldContainer> getFields() {
        return fields;
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
}
