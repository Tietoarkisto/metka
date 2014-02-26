package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.model.data.container.RowContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to move single RowContainer from JSON to UI and back.
 * Values contains either Strings (or whatever is needed to handle non CONTAINER fields) or ContainerTransfer
 * objects when recursive containers are used (e.g. Study Variables).
 */
public class RowTransfer {
    private String key;
    private Integer rowId;

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

    public static RowTransfer buildRowTransferFromRowContaienr(RowContainer containre) {
        return null;
    }
}
