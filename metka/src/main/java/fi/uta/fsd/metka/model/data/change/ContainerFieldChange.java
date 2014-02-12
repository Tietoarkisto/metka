package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerFieldChange  extends FieldChange {
    @XmlElement private Integer nextRowId;
    @XmlElement private final List<RowChange> rows = new ArrayList<>();

    @JsonCreator
    public ContainerFieldChange(@JsonProperty("key") String key) {
        super(key);
    }

    public Integer getNextRowId() {
        return nextRowId;
    }

    public void setNextRowId(Integer nextRowId) {
        this.nextRowId = nextRowId;
    }
}
