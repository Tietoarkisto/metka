package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.model.data.container.ContainerRow;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class TransferRow implements TransferFieldContainer {
    @XmlElement private final String key;
    @XmlElement private Integer rowId;
    @XmlElement private String value = "";
    @XmlElement private LocalDateTime savedAt;
    @XmlElement private String savedBy;
    @XmlElement private final Map<String, TransferField> fields = new HashMap<>();
    @XmlElement private final List<FieldError> errors = new ArrayList<>();
    @XmlElement private Boolean removed;

    @JsonCreator
    public TransferRow(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public Map<String, TransferField> getFields() {
        return fields;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public Boolean getRemoved() {
        return (removed == null ? false : removed);
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null ? false : removed);
    }

    @JsonIgnore
    public boolean hasField(String key) {
        return fields.containsKey(key) && fields.get(key) != null;
    }

    @JsonIgnore
    public TransferField getField(String key) {
        return fields.get(key);
    }

    public void addError(FieldError error) {
        boolean found = false;
        for(FieldError e : errors) {
            if(e == error) {
                found = true;
                break;
            }
        }

        if(!found) {
            errors.add(error);
        }
    }

    public static TransferRow buildFromContainerRow(ContainerRow row) {
        if(row instanceof DataRow) {
            return buildRowFromDataRow((DataRow)row);
        } else if(row instanceof SavedReference) {
            return buildRowFromSavedReference((SavedReference)row);
        }
        return null;
    }

    private static TransferRow buildRowFromDataRow(DataRow row) {
        TransferRow transferRow = new TransferRow(row.getKey());
        transferRow.setRowId(row.getRowId());
        transferRow.setSavedAt(row.getSavedAt());
        transferRow.setSavedBy(row.getSavedBy());
        for(DataField field : row.getFields().values()) {
            TransferField transferField = TransferField.buildFromDataField(field);
            if(transferField != null) {
                transferRow.fields.put(transferField.getKey(), transferField);
            }
        }
        return transferRow;
    }

    private static TransferRow buildRowFromSavedReference(SavedReference row) {
        TransferRow transferRow = new TransferRow(row.getKey());
        transferRow.setRowId(row.getRowId());
        if(row.hasValue()) {
            transferRow.setValue(row.getActualValue());
            transferRow.setSavedAt(row.getReference().getSavedAt());
            transferRow.setSavedBy(row.getReference().getSavedBy());
        }
        return transferRow;
    }
}
