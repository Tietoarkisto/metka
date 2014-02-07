package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ChoicelistType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Choicelist {
    @XmlElement private final String key;
    @XmlElement(name = "default") @JsonProperty("default") private Integer def = 0;
    @XmlElement private Set<Option> options = new LinkedHashSet<Option>();
    @XmlElement private ChoicelistType type = ChoicelistType.VALUE;
    @XmlElement private Reference reference;

    @JsonCreator
    public Choicelist(@JsonProperty("key") String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Integer getDef() {
        return def;
    }

    public void setDef(Integer def) {
        this.def = def;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
    }

    public ChoicelistType getType() {
        return type;
    }

    public void setType(ChoicelistType type) {
        this.type = type;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Choicelist that = (Choicelist) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
