package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.model.ModelBase;

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
public class Configuration implements ModelBase {
    @XmlElement private ConfigurationKey key;
    @XmlElement private final Map<String, Section> sections = new HashMap<>();
    @XmlElement private final Map<String, Reference> references = new HashMap<>();
    @XmlElement private final Map<String, Choicelist> choicelists = new HashMap<>();
    @XmlElement private final Map<String, Field> fields = new HashMap<>();
    @XmlElement private String idField;
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

    public Map<String, Section> getSections() {
        return sections;
    }

    public Map<String, Reference> getReferences() {
        return references;
    }

    public Map<String, Choicelist> getChoicelists() {
        return choicelists;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    // Helper functions
    public Field getField(String key) {
        return fields.get(key);
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
