package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

final class DataFieldAccessor {
    // Private constructor to disable instantiation
    private DataFieldAccessor() {}

    static Pair<StatusCode, DataField> getDataField(Map<String, DataField> fieldMap,
                                                    String key,
                                                    Configuration config,
                                                    ConfigCheck[] configChecks,
                                                    DataFieldOperationChecks.FieldCheck[] fieldChecks) {
        StatusCode result = DataFieldOperationChecks.configChecks(config, key, configChecks);
        if(result != null) return new ImmutablePair<>(result, null);

        DataField field = fieldMap.get(key);
        result = DataFieldOperationChecks.fieldChecks(field, ArrayUtils.add(fieldChecks, DataFieldOperationChecks.FieldCheck.NOT_NULL));
        if(result != null) return new ImmutablePair<>(result, null);

        return new ImmutablePair<>(StatusCode.FIELD_FOUND, field);
    }
}
