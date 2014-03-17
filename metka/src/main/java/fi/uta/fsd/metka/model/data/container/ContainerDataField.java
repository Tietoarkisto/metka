package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerDataField extends DataField {
    @XmlElement private final List<DataRow> rows = new ArrayList<>();

    @JsonCreator
    public ContainerDataField(@JsonProperty("key") String key) {
        super(key);
    }

    public List<DataRow> getRows() {
        return rows;
    }

    @JsonIgnore public DataRow getRow(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return null;
        }
        for(DataRow row : rows) {
            if(row.getRowId().equals(rowId)) {
                return row;
            }
        }
        // Given rowId was not found from this container
        return null;
    }

    @Override
    public DataField copy() {
        ContainerDataField container = new ContainerDataField(getKey());
        for(DataRow row : rows) {
            container.getRows().add((DataRow)row.copy());
        }
        return container;
    }
}
