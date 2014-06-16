package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.DataFieldOperationChecks.*;
import static fi.uta.fsd.metka.model.access.DataFieldAccessor.getDataField;

final class SavedDataFieldAccessor {
    // Private constructor to disable instantiation
    private SavedDataFieldAccessor() {}

    /**
     * Returns requested field from given map as a SavedDataField.
     * If provided with configuration checks the field type to make sure a SavedDataField can be returned.
     * If field exists checks the instanceof value to make sure that the field can be returned as a SavedDataField.
     * This instanceof check is required since it's possible to modify the json-file by hand or batch processing to insert
     * fields that don't conform to configuration.
     * Priority is given to configuration so that if configuration tells us that the field can not be SavedDataField
     * then CONFIG_FIELD_TYPE_MISMATCH status is returned with null value right away even if there is a SavedDataField field present.
     *
     * @param fieldMap Map from where the field should be returned
     * @param key Field key of the requested field
     * @param config Configuration containing the requested field key. Can be null.
     * @return Tuple of StatusCode and SavedDataField. If status is FIELD_FOUND then SavedDataField is the requested field, otherwise SavedDataField is null
     */
    static Pair<StatusCode, SavedDataField> getSavedDataField(Map<String, DataField> fieldMap, String key, Configuration config,
                                                              ConfigCheck[] configChecks) {
        if(fieldMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, DataField> pair = getDataField(
                fieldMap,
                key,
                config,
                ArrayUtils.add(configChecks, ConfigCheck.NOT_CONTAINER),
                new FieldCheck[]{FieldCheck.SAVED_DATA_FIELD});
        return new ImmutablePair<>(pair.getLeft(), (SavedDataField)pair.getRight());
    }
}
