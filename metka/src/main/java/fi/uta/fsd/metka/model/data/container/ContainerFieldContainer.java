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
public class ContainerFieldContainer extends FieldContainer {
    @XmlElement private final List<RowContainer> rows = new ArrayList<>();

    @JsonCreator
    public ContainerFieldContainer(@JsonProperty("key") String key) {
        super(key);
    }

    public List<RowContainer> getRows() {
        return rows;
    }

    @JsonIgnore public RowContainer getRow(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return null;
        }
        for(RowContainer row : rows) {
            if(row.getRowId().equals(rowId)) {
                return row;
            }
        }
        // Given rowId was not found from this container
        return null;
    }

    @Override
    public FieldContainer copy() {
        ContainerFieldContainer container = new ContainerFieldContainer(getKey());
        for(RowContainer row : rows) {
            container.getRows().add((RowContainer)row.copy());
        }
        return container;
    }
}
