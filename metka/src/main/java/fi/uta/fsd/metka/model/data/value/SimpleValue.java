package fi.uta.fsd.metka.model.data.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Every value that is contained in a single textfield, selection or checkbox is saved as a string
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleValue extends Value {
    @XmlElement private final String value;

    @JsonCreator
    public SimpleValue(@JsonProperty("value")String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    @JsonIgnore public boolean hasValue() {
        return !StringUtils.isEmpty(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Value copy() {
        return new SimpleValue(value);
    }
}
