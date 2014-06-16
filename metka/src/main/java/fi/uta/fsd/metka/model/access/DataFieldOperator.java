package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class DataFieldOperator {
    // Disable instantiation
    private DataFieldOperator() {}

    public static <T extends DataField> Pair<StatusCode, T> getDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call) {
        return getDataFieldOperation(fieldMap, call, null);
    }

    @SuppressWarnings("unchecked") // We can suppress unchecked cast warnings since field type is set by the DataFieldCall constructor and always matches up with the generic type
    public static <T extends DataField> Pair<StatusCode, T> getDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call, ConfigCheck[] configChecks) {
        if(fieldMap == null || call == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        switch(call.getFieldType()) {
            case SAVED_DATA_FIELD:
                Pair<StatusCode, SavedDataField> saved = SavedDataFieldAccessor.getSavedDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(saved.getLeft(), (T)saved.getRight());
            case CONTAINER_DATA_FIELD:
                Pair<StatusCode, ContainerDataField> container = ContainerDataFieldAccessor.getContainerDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(container.getLeft(), (T)container.getRight());
            case REFERENCE_CONTAINER_DATA_FIELD:
                Pair<StatusCode, ReferenceContainerDataField> reference = ReferenceContainerDataFieldAccessor.getReferenceContainerDataField(fieldMap, call.getKey(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(reference.getLeft(), (T)reference.getRight());
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }

    public static <T extends DataField> Pair<StatusCode, T> setDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call) {
        return setDataFieldOperation(fieldMap, call, null);
    }

    @SuppressWarnings("unchecked") // We can suppress unchecked cast warnings since field type is set by the DataFieldCall constructor and always matches up with the generic type
    public static <T extends DataField> Pair<StatusCode, T> setDataFieldOperation(Map<String, DataField> fieldMap, DataFieldCall<T> call, ConfigCheck[] configChecks) {
        if(fieldMap == null || call == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
        switch(call.getFieldType()) {
            case SAVED_DATA_FIELD:
                Pair<StatusCode, SavedDataField> saved = SavedDataFieldMutator.setSavedDataField(fieldMap, call.getKey(), call.getValue(), call.getTime(), call.getChangeMap(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(saved.getLeft(), (T)saved.getRight());
            case CONTAINER_DATA_FIELD:
                Pair<StatusCode, ContainerDataField> container = ContainerDataFieldMutator.setContainerDataField(fieldMap, call.getKey(), call.getChangeMap(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(container.getLeft(), (T)container.getRight());
            case REFERENCE_CONTAINER_DATA_FIELD:
                Pair<StatusCode, ReferenceContainerDataField> reference = ReferenceContainerDataFieldMutator.setReferenceContainerDataField(fieldMap, call.getKey(), call.getChangeMap(), call.getConfiguration(), configChecks);
                return new ImmutablePair<>(reference.getLeft(), (T)reference.getRight());
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }
}
