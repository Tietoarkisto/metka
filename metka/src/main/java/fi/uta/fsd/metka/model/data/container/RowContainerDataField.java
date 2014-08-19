package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RowContainerDataField extends DataField {

    private Integer rowIdSeq;

    @JsonCreator
    public RowContainerDataField(@JsonProperty("key") String key, @JsonProperty("type") DataFieldType type, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        super(key, type);
        this.rowIdSeq = rowIdSeq;
    }

    public Integer getRowIdSeq() {
        return rowIdSeq;
    }

    @JsonIgnore public Integer getNewRowId() {
        return rowIdSeq++;
    }
}
