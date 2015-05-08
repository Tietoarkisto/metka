package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;

import java.util.List;
import java.util.Set;
/**
 * Specification and documentation is found from uml/data/uml_json_data_row_container_data_field.graphml
 */
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

    public abstract Set<Integer> getRowIdsFor(Language language);
}