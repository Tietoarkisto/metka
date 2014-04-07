package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ChoicelistType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Choicelist {
    @XmlElement private final String key;
    @XmlElement(name = "default") @JsonProperty("default") private String def = "0";
    @XmlElement private List<Option> options = new ArrayList<>();
    @XmlElement private ChoicelistType type;
    @XmlElement private String reference;
    @XmlElement private Boolean includeEmpty = false;

    @JsonCreator
    public Choicelist(@JsonProperty("key") String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public ChoicelistType getType() {
        return type;
    }

    public void setType(ChoicelistType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getIncludeEmpty() {
        return (includeEmpty == null) ? false : includeEmpty;
    }

    public void setIncludeEmpty(Boolean includeEmpty) {
        this.includeEmpty = (includeEmpty == null) ? false : includeEmpty;
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

    @Override
    public String toString() {
        String out = "Choicelist["+key+"], options[";
        for(Option option : options) {
            out += option;
            out += ", ";
        }
        out += "]";
        return out;
    }
}
