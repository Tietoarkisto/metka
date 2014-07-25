package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
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

/**
 * List of references are saved through this
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceContainerDataField extends RowContainerDataField {
    @XmlElement private final List<SavedReference> references = new ArrayList<>();

    @JsonCreator
    public ReferenceContainerDataField(@JsonProperty("key") String key, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(key, rowIdSeq);
    }

    public List<SavedReference> getReferences() {
        return references;
    }

    @JsonIgnore public SavedReference getReferenceWithId(Integer rowId) {

        if(rowId == null || rowId < 1) {
            // Row can not be found since no rowId given.
            return null;
        }
        for(SavedReference reference : references) {
            if(reference.getRowId().equals(rowId)) {
                return reference;
            }
        }
        // Given rowId was not found from this container
        return null;
    }

    /**
     * Adds reference to list if not already present
     * @param reference
     */
    /*@JsonIgnore
    public void putReference(SavedReference reference) {
        if(reference.getRowId() != null || !getKey().equals(reference.getKey())) {
            if(getReference(reference.getRowId()) == null) {
                references.add(reference);
            }
        }
    }*/

    /**
     * Searches through a list of references for a reference containing given value
     * @param value Reference value that is searched for
     * @return SavedReference matching given value or null if none found
     */
    public Pair<StatusCode, SavedReference> getReferenceWithValue(String value) {
        for(SavedReference reference : references) {
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
     * @param time Time for possible creation of row and field. Can be null
     * @return Tuple of StatusCode and SavedReference. StatusCode tells if the returned row is a new insert or not
     */
    public Pair<StatusCode, SavedReference> getOrCreateReferenceWithValue(String value, Map<String, Change> changeMap, LocalDateTime time) {
        if(changeMap == null || StringUtils.isEmpty(value)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        Pair<StatusCode, SavedReference> pair = getReferenceWithValue(value);
        if(pair.getLeft() == StatusCode.FOUND_ROW) {
            return pair;
        } else {
            if(time == null) {
                time = new LocalDateTime();
            }
            SavedReference reference = SavedReference.build(this);
            references.add(reference);

            ContainerChange change = (ContainerChange)changeMap.get(reference.getKey());
            if(change == null) {
                change = new ContainerChange(reference.getKey());
                changeMap.put(change.getKey(), change);
            }
            reference.setValueToSimple(value, time, change);
            return new ImmutablePair<>(StatusCode.NEW_ROW, reference);
        }
    }

    @Override
    @JsonIgnore
    public DataField copy() {
        ReferenceContainerDataField container = new ReferenceContainerDataField(getKey(), getRowIdSeq());
        for(SavedReference reference : references) {
            container.references.add(reference.copy());
        }
        return container;
    }

    @Override
    public void normalize() {
        List<SavedReference> remove = new ArrayList<>();
        for(SavedReference reference : references) {
            if(reference.isRemoved()) {
                remove.add(reference);
            } else {
                reference.normalize();
            }
        }
        for(SavedReference reference : remove) {
            references.remove(reference);
        }
    }
}
