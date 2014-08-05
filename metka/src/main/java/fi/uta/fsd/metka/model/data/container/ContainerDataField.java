package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerDataField extends RowContainerDataField {
    @XmlElement private final List<DataRow> rows = new ArrayList<>();

    @JsonCreator
    public ContainerDataField(@JsonProperty("key") String key, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(key, rowIdSeq);
    }

    public List<DataRow> getRows() {
        return rows;
    }

    /**
     * Creates a new row and inserts it to this ContainerDataField and adds a change to changeMap
     * @param changeMap
     * @return
     */
    public Pair<StatusCode, DataRow> insertNewDataRow(Map<String, Change> changeMap) {
        if(changeMap == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        ContainerChange change;
        if(!changeMap.containsKey(getKey())) {
            change = new ContainerChange(getKey());
            changeMap.put(change.getKey(), change);
        } else {
            change = (ContainerChange)changeMap.get(getKey());
        }

        DataRow row = DataRow.build(this);
        change.put(new RowChange(row.getRowId()));
        rows.add(row);
        return new ImmutablePair<>(StatusCode.NEW_ROW, row);
    }

    /**
     * Searches through a list of rows for a row with given rowId
     * @param rowId Row id to be searched for amongst rows
     * @return DataRow matching given value or null if none found
     */
    public Pair<StatusCode, DataRow> getRowWithId(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        for(DataRow row : rows) {
            if(row.getRowId().equals(rowId)) {
                return new ImmutablePair<>(StatusCode.FOUND_ROW, row);
            }
        }
        // Given rowId was not found from this container
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    /**
     * Searches through a list of rows for a row containing given value in a field with given id.
     *
     * @param key Field key of field where value should be found
     * @param value Value that is searched for
     * @return DataRow that contains given value in requested field, or null if not found.
     */
    public Pair<StatusCode, DataRow> getRowWithFieldValue(String key, String value) {
        for(DataRow row : rows) {
            Pair<StatusCode, SavedDataField> pair = row.dataField(SavedDataFieldCall.get(key));
            if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                continue;
            }
            SavedDataField field = pair.getRight();
            if(field.valueEquals(value)) {
                return new ImmutablePair<>(StatusCode.FOUND_ROW, row);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Uses getRowWithFieldValue to search for existing row with given value in a given field.
     * If row is not found creates a new row and inserts it to the list.
     * Since it can be assumed that it's desirable to find the field with the given value from the rows list
     * the field is created on the row with the given value
     *
     * @param key Field key of the field where the value should be found
     * @param value Value that is searched for
     * @param changeMap Map where the container change containing this rows changes should reside
     * @param time Time for possible creation of row and field. Can be null
     * @return Tuple of StatusCode and DataRow. StatusCode tells if the returned row is a new insert or not
     */
    public Pair<StatusCode, DataRow> getOrCreateRowWithFieldValue(String key, String value, Map<String, Change> changeMap, LocalDateTime time) {
        if(changeMap == null || StringUtils.isEmpty(value)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, DataRow> pair = getRowWithFieldValue(key, value);
        if(pair.getLeft() == StatusCode.FOUND_ROW) {
            return pair;
        } else {
            if(time == null) {
                time = new LocalDateTime();
            }
            DataRow row = DataRow.build(this);
            rows.add(row);

            row.dataField(SavedDataFieldCall.set(key).setTime(time).setValue(value).setChangeMap(changeMap));
            return new ImmutablePair<>(StatusCode.NEW_ROW, row);
        }
    }

    @Override
    public DataField copy() {
        ContainerDataField container = new ContainerDataField(getKey(), getRowIdSeq());
        for(DataRow row : rows) {
            container.rows.add(row.copy());
        }
        return container;
    }

    @Override
    public void normalize() {
        List<DataRow> remove = new ArrayList<>();
        // If row is removed mark it for removal, otherwise normalize row
        for(DataRow row : rows) {
            if(row.isRemoved()) {
                remove.add(row);
            } else {
                row.normalize();
            }
        }
        // Remove all rows marked for removal
        for(DataRow row : remove) {
            rows.remove(row);
        }
    }
}
