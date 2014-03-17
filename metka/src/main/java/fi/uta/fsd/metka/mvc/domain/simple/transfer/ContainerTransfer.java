package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import org.json.JSONObject;

public class ContainerTransfer {
    /*private String key;
    private Integer nextRowId;
    private final List<RowTransfer> rows = new ArrayList<>();


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getNextRowId() {
        return nextRowId;
    }

    public void setNextRowId(Integer nextRowId) {
        this.nextRowId = nextRowId;
    }

    public List<RowTransfer> getRows() {
        return rows;
    }


    public static ContainerTransfer buildContainerTransfer(ContainerDataField field) {
        ContainerTransfer ct = new ContainerTransfer();
        ct.setKey(field.getKey());
        for(DataRow row : field.getRows()) {
            RowTransfer rt = RowTransfer.buildRowTransferFromRowContaienr(row);
            if(rt != null) {
                ct.getRows().add(rt);
            }
        }
        return ct;
    }*/

    public static JSONObject buildJSONObject(ContainerDataField field) {
        JSONObject json = new JSONObject();
        json.put("type", "container");
        json.put("key", field.getKey());

        for(DataRow row : field.getRows()) {
            JSONObject rt = RowTransfer.buildJSONObject(row);
            if(rt != null) {
                json.append("rows", rt);
            }
        }
        return json;
    }
}
