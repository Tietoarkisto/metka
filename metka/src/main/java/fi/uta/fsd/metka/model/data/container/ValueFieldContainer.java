package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.value.Value;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ValueFieldContainer extends FieldContainer {
    @XmlElement private DateTime savedAt;
    @XmlElement private String savedBy;
    @XmlElement private Value value;

    @JsonCreator
    public ValueFieldContainer(@JsonProperty("key") String key) {
        super(key);
    }

    public DateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(DateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
