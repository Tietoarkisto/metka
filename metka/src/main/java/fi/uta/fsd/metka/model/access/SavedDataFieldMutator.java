package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.SavedDataFieldAccessor.getSavedDataField;

final class SavedDataFieldMutator {
    // Private constructor to disable instantiation
    private SavedDataFieldMutator() {}

    /**
     * Sets provided value to provided DataField map with the provided key.
     * Checks to make sure the field should record a SavedDataField.
     * If no configuration is provided defaults to simple field setting without configuration check.
     * Returns the set field so this can be used to create new fields also.
     * NOTICE: If given value is null and there was no field previously then no new field is created. Setting null value should only be reserved for removal of existing values
     *
     * @param fieldMap Map of DataFields where value should be inserted
     * @param key Field key
     * @param value Value to be inserted in field
     * @param time LocalDateTime for the saved value, can be null
     * @param changeMap Map of changes where insertion should be recorded
     * @param config Configuration where given field key should be found
     * @return Tuple of StatusCode and SavedDataField. If status is FIELD_INSERT or FIELD_UPDATE then SavedDataField is the field that was set,
     *         if status is NO_CHANGE_IN_VALUE then SavedDataField is null if given value is empty otherwise SavedDataField is null
     */
    static Pair<StatusCode, SavedDataField> setSavedDataField(Map<String, DataField> fieldMap, String key, String value, LocalDateTime time,
                                                              Map<String, Change> changeMap, Configuration config,
                                                              ConfigCheck[] configChecks) {
        if(fieldMap == null || changeMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, SavedDataField> pair = getSavedDataField(fieldMap, key, config, configChecks);
        if(pair.getRight() == null && pair.getLeft() != StatusCode.FIELD_MISSING) {
            return pair;
        }
        if(pair.getLeft() == StatusCode.FIELD_MISSING && StringUtils.isEmpty(value)) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
        }

        SavedDataField field = pair.getRight();
        // No previous field, create SavedDataField, set value and return
        if(field == null) {
            field = SavedDataField.build(key).setValueToSimple(value, time, changeMap);
            fieldMap.put(key, field);
            return new ImmutablePair<>(StatusCode.FIELD_INSERT, field);
        }

        // New value is empty and old value is empty, no change in value
        if(StringUtils.isEmpty(value) && (!field.hasValue() || StringUtils.isEmpty(field.getActualValue()))) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, field);
        }

        // Old value is not empty and equals new value, no change in value
        if(field.hasValue() && !StringUtils.isEmpty(field.getActualValue()) && field.getActualValue().equals(value)) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, field);
        }

        // Change in value, update
        field.setValueToSimple(value, time, changeMap);
        return new ImmutablePair<>(StatusCode.FIELD_UPDATE, field);
    }
}
