package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import org.json.JSONObject;

public class ContainerTransfer {
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

    public static JSONObject buildJSONObject(ReferenceContainerDataField field) {
        JSONObject json = new JSONObject();
        json.put("type", "referencecontainer");
        json.put("key", field.getKey());
        for(SavedReference reference : field.getReferences()) {
            JSONObject srt = RowTransfer.buildJSONObject(reference);
            if(srt != null) {
                json.append("references", srt);
            }
        }
        return json;
    }
}
