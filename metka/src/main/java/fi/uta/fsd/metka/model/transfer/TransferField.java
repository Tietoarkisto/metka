package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.TransferFieldType;
import fi.uta.fsd.metka.model.data.container.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TransferField {
    @XmlElement private final String key;
    @XmlElement private final TransferFieldType type;
    @XmlElement private TransferValue value;
    @XmlElement private final List<TransferRow> rows = new ArrayList<>();
    @XmlElement private final List<FieldError> errors = new ArrayList<>();

    @JsonCreator
    public TransferField(@JsonProperty("key")String key, @JsonProperty("type")TransferFieldType type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public TransferFieldType getType() {
        return type;
    }

    public TransferValue getValue() {
        return value;
    }

    public void setValue(TransferValue value) {
        this.value = value;
    }

    public List<TransferRow> getRows() {
        return rows;
    }

    public List<FieldError> getErrors() {
        return errors;
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

    @JsonIgnore
    public TransferRow getRow(Integer rowId) {
        for(TransferRow row : rows) {
            if(row.getRowId() == rowId) {
                return row;
            }
        }
        return null;
    }

    public static TransferField buildFromDataField(DataField field) {
        if(field instanceof SavedDataField) {
            return buildValueFieldFromSavedDataField((SavedDataField) field);
        } else {
            return buildContainerFromDataField(field);
        }
    }

    private static TransferField buildValueFieldFromSavedDataField(SavedDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.VALUE);
        TransferValue value = new TransferValue();
        if(field.hasOriginalValue()) {
            value.setOriginal(field.getOriginalValue().getActualValue());
        }
        if(field.hasModifiedValue()) {
            value.setCurrent(field.getModifiedValue().getActualValue());
        }
        transferField.setValue(value);
        return transferField;
    }

    private static TransferField buildContainerFromDataField(DataField field) {
        if(field instanceof ContainerDataField) {
            return buildContainerFromContainerDataField((ContainerDataField)field);
        } else if(field instanceof ReferenceContainerDataField) {
            return buildContainerFromReferenceContainerDataField((ReferenceContainerDataField)field);
        }
        return null;
    }

    private static TransferField buildContainerFromContainerDataField(ContainerDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.CONTAINER);
        for(DataRow row : field.getRows()) {
            TransferRow transferRow = TransferRow.buildFromContainerRow(row);
            if(transferRow != null) {
                transferField.rows.add(transferRow);
            }
        }
        return transferField;
    }

    private static TransferField buildContainerFromReferenceContainerDataField(ReferenceContainerDataField field) {
        TransferField transferField = new TransferField(field.getKey(), TransferFieldType.REFERENCECONTAINER);
        for(SavedReference reference : field.getReferences()) {
            TransferRow transferRow = TransferRow.buildFromContainerRow(reference);
            if(transferRow != null) {
                transferField.rows.add(transferRow);
            }
        }
        return transferField;
    }
}
