package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * List of references are saved through this
 */
public class ReferenceContainerDataField extends RowContainerDataField {
    private final List<ReferenceRow> references = new ArrayList<>();

    @JsonCreator
    public ReferenceContainerDataField(@JsonProperty("key") String key, @JsonProperty("type") DataFieldType type, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(key, type, rowIdSeq);
    }

    public List<ReferenceRow> getReferences() {
        return references;
    }

    /**
     * Searches through a list of references for a reference with given rowId
     * @param rowId Row id to be searched for amongst references
     * @return SavedReference matching given value or null if none found
     */
    public Pair<StatusCode, ReferenceRow> getReferenceWithId(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        for(ReferenceRow reference : references) {
            if(reference.getRowId().equals(rowId)) {
                return new ImmutablePair<>(StatusCode.FOUND_ROW, reference);
            }
        }
        // Given rowId was not found from this container
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_ID, null);
    }

    /**
     * Searches through a list of references for a reference containing given value
     * @param value Reference value that is searched for
     * @return SavedReference matching given value or null if none found
     */
    public Pair<StatusCode, ReferenceRow> getReferenceWithValue(String value) {
        for(ReferenceRow reference : references) {
            if(reference.valueEquals(value)) {
                return new ImmutablePair<>(StatusCode.FOUND_ROW, reference);
            }
        }
        return new ImmutablePair<>(StatusCode.NO_ROW_WITH_VALUE, null);
    }

    /**
     * Uses getReferenceWithValue to search for existing reference with given value.
     * If reference is not found creates a new reference and inserts it to the list.
     * Since it can be assumed that it's desirable to find the reference with the given value from the references list
     * the reference is created with the given value
     *
     * @param value Value that is searched for
     * @param changeMap Map where the container change containing this rows changes should reside
     * @param info DateTimeUserPair if new reference is needed. If this is null then new instance is used.
     * @return Tuple of StatusCode and SavedReference. StatusCode tells if the returned row is a new insert or not
     */
    public Pair<StatusCode, ReferenceRow> getOrCreateReferenceWithValue(String value, Map<String, Change> changeMap, DateTimeUserPair info) {
        if(changeMap == null || !StringUtils.hasText(value)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        Pair<StatusCode, ReferenceRow> pair = getReferenceWithValue(value);
        if(pair.getLeft() == StatusCode.FOUND_ROW) {
            return pair;
        } else {
            if(info == null) {
                info = DateTimeUserPair.build();
            }
            ReferenceRow reference = ReferenceRow.build(this, new Value(value, ""));
            reference.setSaved(info);
            references.add(reference);

            ContainerChange change = (ContainerChange)changeMap.get(reference.getKey());
            if(change == null) {
                change = new ContainerChange(reference.getKey(), Change.ChangeType.CONTAINER);
                changeMap.put(change.getKey(), change);
            }
            change.put(Language.DEFAULT, new RowChange(reference.getRowId()));
            return new ImmutablePair<>(StatusCode.NEW_ROW, reference);
        }
    }

    @Override
    @JsonIgnore
    public DataField copy() {
        ReferenceContainerDataField container = new ReferenceContainerDataField(getKey(), getType(), getRowIdSeq());
        for(ReferenceRow reference : references) {
            container.references.add(reference.copy());
        }
        return container;
    }

    @Override
    public void normalize() {
        List<ReferenceRow> remove = new ArrayList<>();
        for(ReferenceRow reference : references) {
            if(reference.getRemoved()) {
                remove.add(reference);
            } else {
                reference.normalize();
            }
        }
        for(ReferenceRow reference : remove) {
            references.remove(reference);
        }
    }
}
