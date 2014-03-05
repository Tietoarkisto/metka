package fi.uta.fsd.metka.model.data.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DerivedValue extends SimpleValue {
    @XmlElement private String result;

    @JsonCreator
    public DerivedValue(@JsonProperty("value") String value) {
        super(value);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public Value copy() {
        DerivedValue d = new DerivedValue(getValue());
        d.setResult(result);
        return d;
    }
}
