package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.model.data.container.ContainerFieldContainer;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.container.RowContainer;
import fi.uta.fsd.metka.model.data.container.SavedFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.json.JSONObject;

/**
 * Used to move single RowContainer from JSON to UI and back.
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

    public static RowTransfer buildRowTransferFromRowContaienr(RowContainer container) {
        RowTransfer row = new RowTransfer();
        row.setKey(container.getKey());
        row.setRowId(container.getRowId());
        row.setSavedAt(container.getSavedAt());
        row.setSavedBy(container.getSavedBy());
        for(FieldContainer field : container.getFields().values()) {
            if(field instanceof ContainerFieldContainer) {
                ContainerTransfer ct = ContainerTransfer.buildContainerTransfer((ContainerFieldContainer)field);
                if(ct != null) {
                    row.values.put(ct.getKey(), ct);
                }
            } else {
                SavedFieldContainer saved = (SavedFieldContainer)field;
                // TODO: Handle derived values
                row.values.put(saved.getKey(),((SimpleValue)saved.getValue().getValue()).getValue());
            }
        }
        return row;
    }*/

    public static JSONObject buildJSONObject(RowContainer container) {
        JSONObject json = new JSONObject();
        json.put("type", "row");
        json.put("key", container.getKey());
        json.put("rowId", container.getRowId());
        json.put("savedAt", container.getSavedAt());
        json.put("savedBy", container.getSavedBy());

        JSONObject values = new JSONObject();
        for(FieldContainer field : container.getFields().values()) {
            if(field instanceof ContainerFieldContainer) {
                JSONObject ct = ContainerTransfer.buildJSONObject((ContainerFieldContainer) field);
                if(ct != null) {
                    values.put(field.getKey(), ct);
                }
            } else {
                SavedFieldContainer saved = (SavedFieldContainer)field;
                JSONObject value = new JSONObject();
                value.put("type", "value");
                value.put("value", ((SimpleValue)saved.getValue().getValue()).getValue());
                // TODO: Handle derived values
                values.put(saved.getKey(), value);
            }
        }
        json.put("fields", values);
        return json;
    }
}
