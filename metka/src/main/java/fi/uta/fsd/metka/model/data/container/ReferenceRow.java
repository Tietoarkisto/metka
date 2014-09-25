package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;


/**
 * Single reference in a reference container is saved through this
 */
public class ReferenceRow extends ContainerRow {
    public static ReferenceRow build(ReferenceContainerDataField container, Value reference, DateTimeUserPair info) {
        ReferenceRow row = new ReferenceRow(container.getKey(), container.getNewRowId(), reference);
        row.setSaved(info);
        return row;
    }

    private final Value reference;

    @JsonCreator
    public ReferenceRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId, @JsonProperty("reference") Value reference) {
        super(key, rowId);
        this.reference = reference;
    }

    public Value getReference() {
        return reference;
    }

    @Override
    public void initParents() {}

    /**
     * Convenience method for checking if this reference has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(reference == null) {
            return false;
        } else return reference.hasValue();
    }

    /**
     * Convenience method for checking if the reference equals the given value.
     * NOTICE: Returns false if there is no value.
     *
     * @param compare - Value to compare
     * @return True if the value in this reference equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueEquals(String compare) {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return reference.getValue().equals(compare);
        } else return false;
    }

    /**
     * Convenience method for returning the actual value in this SavedReference.
     * NOTICE: Returns null if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or null if value doesn't exist
     */
    @JsonIgnore
    public String getActualValue() {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return reference.getValue();
        } else return null;
    }

    @JsonIgnore
    public ReferenceRow copy() {
        ReferenceRow row = new ReferenceRow(getKey(), getRowId(), getReference());
        row.setSaved(this.getSaved());
        return row;
    }

    public StatusCode restore(Map<String, Change> changeMap, DateTimeUserPair info) {
        return super.changeStatusFor(Language.DEFAULT, false, changeMap, info);
    }

    public void normalize() {
        // There's nothing to normalize since changing a referenced value of a row doesn't really make sense
        // There either is a value or not and users have to remove a row and create new row to make 'change'
    }
}
