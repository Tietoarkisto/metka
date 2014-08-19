package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.model.data.container.ContainerRow;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferRow implements TransferFieldContainer {
    private final String key;
    private Integer rowId;
    private String value = "";
    private DateTimeUserPair saved;
    private final Map<String, TransferField> fields = new HashMap<>();
    private final List<FieldError> errors = new ArrayList<>();
    private Boolean removed;

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

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
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

    @JsonIgnore
    public void addField(TransferField field) {
        if(hasField(field.getKey())) {
            return;
        }
        fields.put(field.getKey(), field);
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
        // Add common info
        TransferRow transferRow = new TransferRow(row.getKey());
        transferRow.setRowId(row.getRowId());
        transferRow.setSaved(row.getSaved());
        transferRow.setRemoved(row.getRemoved());

        if(row instanceof DataRow) {
            return buildRowFromDataRow(transferRow, (DataRow)row);
        } else if(row instanceof ReferenceRow) {
            return buildRowFromSavedReference(transferRow, (ReferenceRow)row);
        }
        return null;
    }

    private static TransferRow buildRowFromDataRow(TransferRow transferRow, DataRow row) {
        for(DataField field : row.getFields().values()) {
            TransferField transferField = TransferField.buildFromDataField(field);
            if(transferField != null) {
                transferRow.fields.put(transferField.getKey(), transferField);
            }
        }
        return transferRow;
    }

    private static TransferRow buildRowFromSavedReference(TransferRow transferRow, ReferenceRow row) {
        if(row.hasValue()) {
            transferRow.setValue(row.getActualValue());
        }
        return transferRow;
    }
}
