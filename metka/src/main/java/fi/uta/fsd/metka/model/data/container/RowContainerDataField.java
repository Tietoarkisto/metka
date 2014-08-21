package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class RowContainerDataField extends DataField {

    private Integer rowIdSeq;

    public RowContainerDataField(String key, Integer rowIdSeq) {
        super(key);
        this.rowIdSeq = rowIdSeq;
    }

    public Integer getRowIdSeq() {
        return rowIdSeq;
    }

    @JsonIgnore public Integer getNewRowId() {
        return rowIdSeq++;
    }
}
