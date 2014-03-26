package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.json.JSONObject;

/**
 * Used to move single DataRow from JSON to UI and back.
 * Values contains either Strings (or whatever is needed to handle non CONTAINER fields) or ContainerTransfer
 * objects when recursive containers are used (e.g. Study Variables).
 */
public class RowTransfer {
    /*private String key;
    private Integer rowId;
    private DateTime savedAt;
    private String savedBy;

    private final Map<String, Object> values = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public DateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(DateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public static RowTransfer buildRowTransferFromRowContaienr(DataRow container) {
        RowTransfer row = new RowTransfer();
        row.setKey(container.getKey());
        row.setRowId(container.getRowId());
        row.setSavedAt(container.getSavedAt());
        row.setSavedBy(container.getSavedBy());
        for(DataField field : container.getFields().values()) {
            if(field instanceof ContainerDataField) {
                ContainerTransfer ct = ContainerTransfer.buildContainerTransfer((ContainerDataField)field);
                if(ct != null) {
                    row.values.put(ct.getKey(), ct);
                }
            } else {
                SavedDataField saved = (SavedDataField)field;
                // TODO: Handle derived values
                row.values.put(saved.getKey(),saved.getActualValue());
            }
        }
        return row;
    }*/

    public static JSONObject buildJSONObject(DataRow container) {
        JSONObject json = new JSONObject();
        json.put("type", "row");
        json.put("key", container.getKey());
        json.put("rowId", container.getRowId());
        json.put("savedAt", container.getSavedAt());
        json.put("savedBy", container.getSavedBy());

        JSONObject values = new JSONObject();
        for(DataField field : container.getFields().values()) {
            if(field instanceof ContainerDataField) {
                JSONObject ct = ContainerTransfer.buildJSONObject((ContainerDataField) field);
                if(ct != null) {
                    values.put(field.getKey(), ct);
                }
            } else if(field instanceof ReferenceContainerDataField) {
                JSONObject rct = ContainerTransfer.buildJSONObject((ReferenceContainerDataField)field);
                if(rct != null) {
                    values.put(field.getKey(), rct);
                }
            } else {
                SavedDataField saved = (SavedDataField)field;
                JSONObject value = new JSONObject();
                value.put("type", "value");
                value.put("value", saved.getActualValue());
                // TODO: Handle derived values
                values.put(saved.getKey(), value);
            }
        }
        json.put("fields", values);
        return json;
    }

    public static JSONObject buildJSONObject(SavedReference reference) {
        JSONObject json = new JSONObject();
        json.put("type", "reference");
        json.put("key", reference.getKey());
        json.put("rowId", reference.getRowId());
        if(reference.getValue() != null) {
            json.put("value", reference.getActualValue());
            json.put("savedAt", reference.getValue().getSavedAt());
            json.put("savedBy", reference.getValue().getSavedBy());
        } else {
            json.put("value", (Object)null);
            json.put("savedAt", (Object)null);
            json.put("savedBy", (Object)null);
        }
        // TODO: Collect all needed reference information based on subfields
        return json;
    }
}
