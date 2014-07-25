package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.model.data.value.Value;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SavedValue {
    /**
     * Creates and initialises a new SavedValue from given parameters.
     * @param time Time when this container was requested.
     * @return Initialised SavedValue ready for use
     */
    public static SavedValue build(LocalDateTime time) {
        SavedValue value = new SavedValue();
        value.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return value;
    }

    @XmlElement private LocalDateTime savedAt;
    @XmlElement private String savedBy;
    @XmlElement private Value value;

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return (savedBy == null) ? "" : savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return (value == null) ? null : value.toString();
    }

    /*
    * This clears existing value from this SavedValue and inserts a new SimpleValue with the given
    * value.
    *
    * WARNING: This does not in any way check what type of value this field is supposed to have
     *         so you can easily insert illegal values with this.
     */
    @JsonIgnore public SavedValue setToSimpleValue(String value) {
        this.setValue(new SimpleValue(value));
        return this;
    }

    public SavedValue copy() {
        SavedValue saved = new SavedValue();
        saved.setSavedAt(new LocalDateTime(savedAt));
        saved.setSavedBy(savedBy);
        saved.value = value.copy();
        return saved;
    }
}
