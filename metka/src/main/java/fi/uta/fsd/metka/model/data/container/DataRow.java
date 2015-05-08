package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.DataFieldOperator;
import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.calls.DataFieldCallBase;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Specification and documentation is found from uml/data/uml_json_data_field.graphml
 */
public class DataRow extends ContainerRow implements DataFieldContainer {

    @JsonIgnore private DataFieldContainer parent; // This is used for navigation during restriction validation

    public static DataRow build(ContainerDataField container) {
        return new DataRow(container.getKey(), container.getNewRowId());
    }

    private final Map<String, DataField> fields = new HashMap<>();

    @JsonCreator
    public DataRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        super(key, rowId);
    }

    public Map<String, DataField> getFields() {
        return fields;
    }

    @Override
    public DataFieldContainer getParent() {
        return parent;
    }

    @Override
    public void setParent(DataFieldContainer parent) {
        this.parent = parent;
    }

    public StatusCode restore(Map<String, Change> changeMap, Language language, DateTimeUserPair info) {
        return super.changeStatusFor(language, false, changeMap, info);
    }

    @Override
    public void initParents(DataFieldContainer parent) {
        setParent(parent);
        for(DataField field : fields.values()) {
            field.initParents(this);
        }
    }

    @Override
    public void initParents() {
        initParents(null);
    }

    public DataRow copy() {
        DataRow row = new DataRow(getKey(), getRowId());
        row.setSaved(getSaved());
        row.setRemoved(getRemoved());
        for(DataField field : fields.values()) {
            row.fields.put(field.getKey(), field.copy());
        }
        return row;
    }

    @JsonIgnore
    public RevisionKey getRevisionKey() {
        return parent.getRevisionKey();
    }

    @JsonIgnore
    public ConfigurationKey getConfigurationKey() {
        return parent.getConfigurationKey();
    }

    public void normalize() {
        for(DataField field : fields.values()) {
            field.normalize();
        }
    }

    /**
     * Performs DataField related DataFieldCall operations on this row.
     * It is assumed that the changeMap in the call object is the map from where this row's container's change should be found
     * since we don't want to check beforehand if there are changes or not.
     * This method will create the change objects if they are missing and add them to the map if necessary.
     * @param call DataFieldCall object defining what operation to process
     * @return Pair object where left side is a status code describing the result and right side is the resulting DataField object typed by DataFieldCall
     */
    @Override
    public <T extends DataField> Pair<StatusCode, T> dataField(DataFieldCall<T> call) {
        switch(call.getCallType()) {
            case GET:
                return DataFieldOperator.getDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.IS_SUBFIELD});
            case CHECK:
                return DataFieldOperator.checkDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.IS_SUBFIELD});
            case SET:
                if(call.getChangeMap() == null) {
                    // We don't need to continue since this is the result anyway
                    return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
                }
                // Get original change map from the call object and get ContainerChange and RowChange or create them if not present
                Map<String, Change> changeMap = call.getChangeMap();
                ContainerChange container = (ContainerChange)call.getChangeMap().get(getKey());

                if(container == null) {
                    container = new ContainerChange(getKey());
                }

                RowChange row = container.get(getRowId());
                if(row == null) {
                    row = new RowChange(getRowId());
                }
                // Set row's change map to the call object instead
                ((DataFieldCallBase<T>)call).setChangeMap(row.getChanges());
                Pair<StatusCode, T> pair = DataFieldOperator.setDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.IS_SUBFIELD});

                // If field was either inserted or updated then add change to original container
                if(pair.getLeft() == StatusCode.FIELD_INSERT || pair.getLeft() == StatusCode.FIELD_UPDATE ) {
                    container.put(row);
                    changeMap.put(container.getKey(), container);
                }
                return pair;
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }
}
