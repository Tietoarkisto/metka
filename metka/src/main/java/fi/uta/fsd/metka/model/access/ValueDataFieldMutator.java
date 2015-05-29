package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueContainer;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.ValueDataFieldAccessor.getValueDataField;

final class ValueDataFieldMutator {
    // Private constructor to disable instantiation
    private ValueDataFieldMutator() {}

    /**
     * Sets provided value to provided DataField map with the provided key.
     * Checks to make sure the field should record a ValueDataField.
     * If no configuration is provided defaults to simple field setting without configuration check.
     * Returns the set field so this can be used to create new fields also.
     * NOTICE: If given value is null and there was no field previously then no new field is created. Setting null value should only be reserved for removal of existing values
     *
     * @param fieldMap Map of DataFields where value should be inserted
     * @param key Field key
     * @param value Value to be inserted in field
     * @param info DateTimeUserPair for the saved value, can be null
     * @param changeMap Map of changes where insertion should be recorded
     * @param config Configuration where given field key should be found
     * @return Tuple of StatusCode and ValueDataField. If status is FIELD_INSERT or FIELD_UPDATE then ValueDataField is the field that was set,
     *         if status is NO_CHANGE_IN_VALUE then ValueDataField is null if given value is empty otherwise ValueDataField is null
     */
    static Pair<StatusCode, ValueDataField> setValueDataField(Language language, Map<String, DataField> fieldMap, String key, Value value,
                                                              DateTimeUserPair info, Map<String, Change> changeMap, Configuration config,
                                                              ConfigCheck[] configChecks) {
        if(fieldMap == null || changeMap == null || !StringUtils.hasText(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, ValueDataField> pair = getValueDataField(fieldMap, key, config, configChecks);
        if(pair.getLeft() == StatusCode.CONFIG_FIELD_MISSING) {
            return pair;
        }
        if(pair.getRight() == null && pair.getLeft() != StatusCode.FIELD_MISSING) {
            return pair;
        }
        if((pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(language)) && !value.hasValue()) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
        }

        if(config != null) {
            // We know that we have the field in config since getValueDataField would have returned CONFIG_FIELD_MISSING otherwise
            Field field = config.getField(key);
            if(!field.getTranslatable() && language != Language.DEFAULT) {
                return new ImmutablePair<>(StatusCode.FIELD_NOT_TRANSLATABLE, pair.getRight());
            }
        }

        if(pair.getLeft() == StatusCode.FIELD_FOUND) {
            // Old value is not empty and equals new value, no change in value
            if(pair.getRight().valueForEquals(language, value.getValue())) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, pair.getRight());
            }
            // Old value is empty and new value is either null or empty
            if(!pair.getRight().hasValueFor(language) && (value.isNull() || value.isEmpty())) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, pair.getRight());
            }
        }

        if(info == null) {
            info = DateTimeUserPair.build();
        }

        //NOTE: We should at some point make sure that the value is valid for field type eg. DATE field shouldn't contain TIME element

        // No previous field, create ValueDataField, set value and return
        StatusCode statusCode;
        if(pair.getLeft() == StatusCode.FIELD_MISSING) {
            ValueDataField field = ValueDataField.build(key);
            field.setCurrentFor(language, ValueContainer.build(info, value));
            fieldMap.put(key, field);

            statusCode = StatusCode.FIELD_INSERT;
            pair = new ImmutablePair<>(statusCode, field);
        } else if(pair.getLeft() == StatusCode.FIELD_FOUND) {
            pair.getRight().setCurrentFor(language, ValueContainer.build(info, value));
            statusCode = StatusCode.FIELD_UPDATE;
        } else {
            return pair;
        }

        if(changeMap.get(key) == null) {
            changeMap.put(key, new Change(key));
        }
        changeMap.get(key).setChangeIn(language);
        return new ImmutablePair<>(statusCode, pair.getRight());
    }
}
