package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * List of references are saved through this
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceContainerDataField extends DataField {
    @XmlElement private final List<SavedReference> references = new ArrayList<>();

    @JsonCreator
    public ReferenceContainerDataField(@JsonProperty("key") String key) {
        super(key);
    }

    public List<SavedReference> getReferences() {
        return references;
    }

    @JsonIgnore public SavedReference getReference(Integer rowId) {

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
    @JsonIgnore
    public void putReference(SavedReference reference) {
        if(reference.getRowId() != null || !getKey().equals(reference.getKey())) {
            if(getReference(reference.getRowId()) == null) {
                references.add(reference);
            }
        }
    }

    @Override
    @JsonIgnore
    public DataField copy() {
        ReferenceContainerDataField container = new ReferenceContainerDataField(getKey());
        for(SavedReference reference : references) {
            container.getReferences().add(reference.copy());
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
