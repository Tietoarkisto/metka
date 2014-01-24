package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldContainer {
    @XmlElement(name="key") @JsonProperty("key") private final String fieldKey;
    @XmlElement private DateTime savedAt;
    @XmlElement private String savedBy;
    @XmlElement private List<Value> values = new ArrayList<Value>();

    @JsonCreator
    public FieldContainer(@JsonProperty("key")String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldKey() {
        return fieldKey;
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

    public List<Value> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldContainer that = (FieldContainer) o;

        if (!fieldKey.equals(that.fieldKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fieldKey.hashCode();
    }
}
