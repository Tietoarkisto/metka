package fi.uta.fsd.metka.model.interfaces;

import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.apache.commons.lang3.tuple.Pair;

public interface DataFieldContainer {
    <T extends DataField> Pair<StatusCode, T> dataField(DataFieldCall<T> call);
}
