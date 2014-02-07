package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Configuration {
    @XmlElement private ConfigurationKey key;
    @XmlElement private Set<Section> sections = new HashSet<Section>();
    @XmlElement private Map<String, Choicelist> choicelists = new HashMap<>();
    @XmlElement private Map<String, Field> fields = new HashMap<>();
    @XmlElement private String hash; // no functionality for hash is implemented at this time.

    public Configuration() {
    }

    public Configuration(ConfigurationKey key) {
        this.key = key;
    }

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    public Set<Section> getSections() {
        return sections;
    }

    public Map<String, Choicelist> getChoicelists() {
        return choicelists;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
