package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerFieldContainer extends FieldContainer {
    @XmlElement private Integer nextRowId = 1;
    @XmlElement private final List<RowContainer> rows = new ArrayList<>();

    @JsonCreator
    public ContainerFieldContainer(@JsonProperty("key") String key) {
        super(key);
    }

    public Integer getNextRowId() {
        return nextRowId;
    }

    public void setNextRowId(Integer nextRowId) {
        this.nextRowId = nextRowId;
    }

    public List<RowContainer> getRows() {
        return rows;
    }
}
