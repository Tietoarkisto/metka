package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


/**
 * Contains static methods for handling DataFields and DataRows in RevisionData objects.
 * Mostly consists of different getters and setters that provide unified error checking.
 * Also provides simplified methods for situations where the error checking is redundant
 * or for situations where configuration is not provided and config checking is skipped.
 *
 * TODO: Get rid of redundant operations as much as possible
 */
public final class ModelFieldUtil {
    // Private constructor to stop instantiation
    private ModelFieldUtil() {}

    // **************
    // Row operations
    // **************

    /**
     * Marks a row for removal and adds a change if change is missing
     * @param row DataRow to be marked for removal
     * @param changeMap Map that should contain the change for container where given row is present
     */
    public static void removeRow(DataRow row, Map<String, Change> changeMap) {
        // Sanity check
        if(row == null || changeMap == null || row.isRemoved()) {
            // If row or changeMap are not present or if the row is already removed there's no point in continuing
            return;
        }

        row.setRemoved(true);
        ContainerChange containerChange = (ContainerChange)changeMap.get(row.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(row.getKey());
            changeMap.put(row.getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(row.getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(row.getRowId());
            containerChange.put(rowChange);
        }
    }

    /**
     * Marks a reference for removal and adds a change if change is missing.
     * @param reference SavedReference to be marked for removal
     * @param changeMap Map that should contain the change for container where given row is present
     */
    public static void removeReference(SavedReference reference, Map<String, Change> changeMap) {
        if(reference == null || changeMap == null || reference.isRemoved()) {
            return;
        }

        reference.setRemoved(true);
        ContainerChange containerChange = (ContainerChange)changeMap.get(reference.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(reference.getKey());
            changeMap.put(reference.getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(reference.getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(reference.getRowId());
            containerChange.put(rowChange);
        }
    }

    /**
     * Searches through a list of rows for a row containing given value in a field with given id.
     *
     * @param rows List of rows to search through
     * @param key Field key of field where value should be found
     * @param value Value that is searched for
     * @return DataRow that contains given value in requested field, or null if not found.
     */
    public static DataRow findRowWithFieldValue(List<DataRow> rows, String key, String value) {
        for(DataRow row : rows) {
            Pair<StatusCode, SavedDataField> pair = row.dataField(SavedDataFieldCall.get(key));
            SavedDataField field = pair.getRight();
            if(field != null && field.hasValue() && field.valueEquals(value)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Searches through a list of references for a reference containing given value
     * @param references List of references to search through
     * @param value Reference value that is searched for
     * @return SavedReference matching given value or null if none found
     */
    public static SavedReference findReferenceWithValue(List<SavedReference> references, String value) {
        for(SavedReference reference : references) {
            if(reference.valueEquals(value)) {
                return reference;
            }
        }
        return null;
    }

    public static Pair<StatusCode, SavedReference> findOrCreateReferenceWithValue(RevisionData revision, ReferenceContainerDataField references, String value, LocalDateTime time) {
        return findOrCreateReferenceWithValue(revision, references, value, revision.getChanges(), time);
    }

    public static Pair<StatusCode, SavedReference> findOrCreateReferenceWithValue(RevisionData revision, ReferenceContainerDataField references, String value, Map<String, Change> changeMap, LocalDateTime time) {
        if(revision == null || references == null || changeMap == null || StringUtils.isEmpty(value)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        SavedReference reference = findReferenceWithValue(references.getReferences(), value);
        StatusCode status;
        if(reference == null) {
            if(time == null) {
                time = new LocalDateTime();
            }
            reference = new SavedReference(references.getKey(), revision.getNewRowId());
            references.putReference(reference);
            ContainerChange change = (ContainerChange)changeMap.get(reference.getKey());
            if(change == null) {
                change = new ContainerChange(reference.getKey());
                changeMap.put(change.getKey(), change);
            }
            reference.setValueToSimple(value, time, change);
            status = StatusCode.NEW_REFERENCE;
        } else {
            status = StatusCode.FOUND_REFERENCE;
        }
        return new ImmutablePair<>(status, reference);
    }

    /**
     * Uses findRowWithFieldValue to search for existing row in given rows list.
     * If row is not found creates a new row and inserts it to the list.
     * Since it can be assumed that it's desirable to find the field with the given value from the rows list
     * the field is created on the row with the given value
     *
     * @param revision Revision containing this row. Needed to generate new row id if required
     * @param container ContainerDataField where row should be or where new row is inserted
     * @param key Field key of the field where the value should be found
     * @param value Value that is searched for
     * @param changeMap Map where the container change containing this rows changes should reside
     * @param time Time for possible creation of row and field. Can be null
     * @return Tuple of StatusCode and DataRow. StatusCode tells if the returned row is a new insert or not
     */
    public static Pair<StatusCode, DataRow> findOrCreateRowWithFieldValue(RevisionData revision, ContainerDataField container, String key, String value, Map<String, Change> changeMap, LocalDateTime time) {
        DataRow row = findRowWithFieldValue(container.getRows(), key, value);
        StatusCode status;
        if(row == null) {
            if(time == null) {
                time = new LocalDateTime();
            }
            row = new DataRow(container.getKey(), revision.getNewRowId());
            container.putRow(row);
            row.dataField(SavedDataFieldCall.set(key).setTime(time).setValue(value).setChangeMap(changeMap));
            status = StatusCode.NEW_ROW;
        } else {
            status = StatusCode.FOUND_ROW;
        }

        return new ImmutablePair<StatusCode, DataRow>(status, row);
    }
}
