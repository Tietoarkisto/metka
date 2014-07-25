package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RowContainerDataField extends DataField {

    @XmlElement
    private Integer rowIdSeq;

    @JsonCreator
    public RowContainerDataField(@JsonProperty("key") String key, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(key);
        this.rowIdSeq = rowIdSeq;
    }

    public Integer getRowIdSeq() {
        return rowIdSeq;
    }

    @JsonIgnore public Integer getNewRowId() {
        return rowIdSeq++;
    }
}
