package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.uta.fsd.metka.data.util.ModelAccessUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DataField implements ModelAccessUtil.PathNavigable {
    @XmlElement private final String key;

    @JsonCreator
    public DataField(@JsonProperty("key") String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataField that = (DataField) o;

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

    @JsonIgnore
    public DataField copy() {throw new UnsupportedOperationException();}
    @JsonIgnore
    public void normalize() {throw new UnsupportedOperationException();}
}
