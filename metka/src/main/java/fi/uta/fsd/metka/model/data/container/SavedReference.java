package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.model.interfaces.Row;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.Map;


/**
 * Single reference in a reference container is saved through this
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SavedReference extends ContainerRow implements Row {
    public static SavedReference build(ReferenceContainerDataField container) {
        return new SavedReference(container.getKey(), container.getNewRowId());
    }
    @XmlElement private SavedValue reference;

    @JsonCreator
    public SavedReference(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        super(key, rowId);
    }

    public SavedValue getReference() {
        return reference;
    }

    public void setReference(SavedValue reference) {
        this.reference = reference;
    }

    /**
     * Convenience method for setting the reference value.
     * Creates a new Simple value with provided value and sets modified value to that.
     * Returns this instance to facilitate chaining
     * @param value String to be set to SimpleValue
     * @param time LocalDateTime object to give to SimpleValue. Can be null in which case a new time instance is created
     * @param change ContainerChange for the ReferenceContainer that contains this SavedReference
     * @return reference to this instance
     */
    @JsonIgnore
    public SavedReference setValueToSimple(String value, LocalDateTime time, ContainerChange change) {
        if(time == null) {
            time = new LocalDateTime();
        }
        this.reference = SavedValue.build(time).setToSimpleValue(value);
        change.put(new RowChange(getRowId()));
        return this;
    }

    /**
     * Convenience method for checking if this reference has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(reference == null || reference.getValue() == null) {
            return false;
        } else return true;
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
            return ((SimpleValue)reference.getValue()).getValue().equals(compare);
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
            return ((SimpleValue)reference.getValue()).getValue();
        } else return null;
    }

    @JsonIgnore
    public SavedReference copy() {
        SavedReference reference = new SavedReference(getKey(), getRowId());
        reference.reference = this.reference;
        return reference;
    }

    @Override
    public void remove(Map<String, Change> changeMap) {
        if(changeMap == null || isRemoved()) {
            return;
        }

        setRemoved(true);
        ContainerChange containerChange = (ContainerChange)changeMap.get(getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(getKey());
            changeMap.put(getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(getRowId());
            containerChange.put(rowChange);
        }
    }

    public void normalize() {
        // There's nothing to normalize since changing a referenced value of a row doesn't really make sense
        // There either is a value or not and users have to remove a row and create new row to make 'change'
    }
}
