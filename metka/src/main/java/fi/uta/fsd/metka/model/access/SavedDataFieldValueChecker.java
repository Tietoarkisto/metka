package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.SavedDataFieldAccessor.getSavedDataField;

final class SavedDataFieldValueChecker {
    /**
     * Private constructor to disable instantiation.
     */
    private SavedDataFieldValueChecker() {}

    /**
     * Return what would happen if given value would be set to given DataField
     * map with given key.
     *
     * @param fieldMap      map
     * @param key           key
     * @param value         value
     * @param config        config
     * @param configChecks  config checks
     * @return              status code and saved data field pair
     */
    static Pair<StatusCode, SavedDataField> checkSavedDataFieldValue(Map<String, DataField> fieldMap, String key, String value, Configuration config, ConfigCheck[] configChecks) {
        // Null maps or empty key results in incorrect parameters
        if(fieldMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Get status code and saved data field pair
        Pair<StatusCode, SavedDataField> pair = getSavedDataField(fieldMap, key, config, configChecks);

        // Status code is never null but saved data field can be null
        StatusCode statusCode = pair.getLeft();

        // Field does not have configuration
        if (statusCode == StatusCode.CONFIG_FIELD_MISSING) {
            // Case where f.ex. it should not be written to revision data
            return pair;
        }

        // Field has configuration but does not exist
        if (statusCode == StatusCode.FIELD_MISSING) {
            // Config and field for key in config can be null

            // If value is null or empty no change
            if ( StringUtils.isEmpty(value) ) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            if (config != null  && config.getField(key) != null) {
                // Check field configuration
                Field field = config.getField(key);

                // If false value should not be written in revision data
                if ( !field.getWritable() ) {
                    // Field is not writable
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_WRITABLE
                            , null);
                }

                // If false user cannot edit value
                if ( !field.getEditable() ) {
                    // Field is not editable by user
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_EDITABLE
                            , null);
                }
                // No existing value so immutability does not matter
            }

            // Field is writable and editable and value is not null or empty
            // results in field insert. Saved data field is null in this case
            return new ImmutablePair<>(StatusCode.FIELD_INSERT, null);
        }

        // Field found
        if (statusCode == StatusCode.FIELD_FOUND) {
            // Config and field for key in config can be null but saved data
            // field is not null
            SavedDataField savedDataField = pair.getRight();

            // Old and new value null or empty results in no change
            if (StringUtils.isEmpty(value) && !savedDataField.hasValue()) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            // Old and new value equals results in no change
            if (savedDataField.hasValue() && savedDataField.getActualValue().equals(value) ) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
            }

            if (config != null  && config.getField(key) != null) {
                // Check field configuration
                Field field = config.getField(key);

                // If false value should not be written in revision data
                if(!field.getWritable() ) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_WRITABLE, null);
                }

                // If false user cannot edit value
                if(!field.getEditable()) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_EDITABLE, null);
                }

                // Check mutability
                if(field.getImmutable()
                        && savedDataField.hasOriginalValue()
                        && !savedDataField.getOriginalValue().getActualValue().equals(value)) {
                    return new ImmutablePair<>(StatusCode.FIELD_NOT_MUTABLE, null);
                }
            }

            // Field is writable, mutable and editable results in field update
            return new ImmutablePair<>(StatusCode.FIELD_UPDATE, savedDataField);
        }

        // Catch all other status codes ?
        return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
    }

}
