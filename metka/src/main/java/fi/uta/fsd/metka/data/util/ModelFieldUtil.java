package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static fi.uta.fsd.metka.data.util.ModelValueUtil.*;

/**
 * Contains static methods for handling DataFields and DataRows in RevisionData objects.
 * Mostly consists of different getters and setters that provide unified error checking.
 * Also provides simplified methods for situations where the error checking is redundant
 * or for situations where configuration is not provided and config checking is skipped.
 *
 * TODO: Get rid of redundant operations as much as possible
 */
public class ModelFieldUtil {

    public static enum StatusCode {
        FIELD_FOUND,                        // Field was found, it was successfully determined to be SavedDataField and was returned as such
        FIELD_MISSING,                      // Field can be SavedDataField but no field with the given key was found
        FIELD_UPDATE,                       // SavedDataField was found, it can exist (not prohibited by configuration or existing field of different type) and was updated
        FIELD_INSERT,                       // SavedDataField can exist (not prohibited by configuration) but no previous field was found. New field was created
        NO_CHANGE_IN_VALUE,                 // Either SavedDataField was found but the value remained the same or there was no previous field and the value was null.
        CONFIG_FIELD_MISSING,               // There's no field configuration for the given key in the configuration provided
        CONFIG_FIELD_TYPE_MISMATCH,         // Configuration tells us that the field can not be SavedDataField
        CONFIG_FIELD_LEVEL_MISMATCH,        // Requested field was not at right level, either a top level field was requested and config says field is a subfield or the other way around
        FIELD_TYPE_MISMATCH,                // Found field did not pass instanceof check
        INCORRECT_PARAMETERS,               // Some of the parameters provided to the method were not sufficient or were in some way incorrect
        FOUND_ROW,                          // Used to indicate that old row was used with the request
        NEW_ROW                             // Used to indicate that a new row was created with the request
    }

    private static enum ConfigCheck {
        IS_CONTAINER,           // Field has to be one of container types
        NOT_CONTAINER,          // Field must not be one of container types
        IS_SUBFIELD,            // Field must be a subfield
        NOT_SUBFIELD,           // Field must not be a subfield
        TYPE_CONTAINER,         // Field must be of type CONTAINER
        TYPE_REFERENCECONTAINER // Field must be of type REFERENCECONTAINER
    }

    /**
     * Different checks that can be done to field.
     * Notice that NULL status must be checked separately, all of the type checks only
     * proceed if the field is not null, otherwise they are skipped.
     */
    private static enum FieldCheck {
        NOT_NULL,                       // Field must not be null
        SAVED_DATA_FIELD,               // If field is not null checks to see if it's an instance of SavedDataField
        CONTAINER_DATA_FIELD,           // If field is not null checks to see if it's an instance of ContainerDataField
        REFERENCE_CONTAINER_DATA_FIELD  // If field is not null checks to see if it's an instance of ReferenceContainerDataField
    }

    // **********************
    // Private helper methods
    // **********************

    /**
     * Does configuration checking based on given parameters.
     * Returns appropriate error messages if fault is detected or null if everything is OK or the config is missing
     *
     * @param config Configuration where field should be found
     * @param key Field key of the field to be checked
     * @param checks All special conditions to check for this config and field.
     * @return StatusCode with the possible error in checking or null if no errors detected
     */
    private static StatusCode configChecks(Configuration config, String key, ConfigCheck... checks) {
        if(config != null) {
            Field field = config.getField(key);
            if(field == null) {
                return StatusCode.CONFIG_FIELD_MISSING;
            }
            for(ConfigCheck check : checks) {
                switch(check) {
                    case IS_SUBFIELD:
                        if(!field.getSubfield()) {
                            return StatusCode.CONFIG_FIELD_LEVEL_MISMATCH;
                        }
                        break;
                    case NOT_SUBFIELD:
                        if(field.getSubfield()) {
                            return StatusCode.CONFIG_FIELD_LEVEL_MISMATCH;
                        }
                        break;
                    case IS_CONTAINER:
                        if(!field.getType().isContainer()) {
                            return StatusCode.CONFIG_FIELD_TYPE_MISMATCH;
                        }
                        break;
                    case NOT_CONTAINER:
                        if(field.getType().isContainer()) {
                            return StatusCode.CONFIG_FIELD_TYPE_MISMATCH;
                        }
                        break;
                    case TYPE_CONTAINER:
                        if(field.getType() != FieldType.CONTAINER) {
                            return StatusCode.CONFIG_FIELD_TYPE_MISMATCH;
                        }
                        break;
                    case TYPE_REFERENCECONTAINER:
                        if(field.getType() != FieldType.REFERENCECONTAINER) {
                            return StatusCode.CONFIG_FIELD_TYPE_MISMATCH;
                        }
                        break;
                }
            }
        }
        return null;
    }

    private static StatusCode fieldChecks(DataField field, FieldCheck... checks) {
        for(FieldCheck check : checks) {
            switch(check) {
                case NOT_NULL:
                    if(field == null) {
                        return StatusCode.FIELD_MISSING;
                    }
                    break;
                case SAVED_DATA_FIELD:
                    if(field != null && !(field instanceof SavedDataField)) {
                        return StatusCode.FIELD_TYPE_MISMATCH;
                    }
                    break;
                case CONTAINER_DATA_FIELD:
                    if(field != null && !(field instanceof ContainerDataField)) {
                        return StatusCode.FIELD_TYPE_MISMATCH;
                    }
                    break;
                case REFERENCE_CONTAINER_DATA_FIELD:
                    if(field != null && !(field instanceof ReferenceContainerDataField)) {
                        return StatusCode.FIELD_TYPE_MISMATCH;
                    }
                    break;
            }
        }
        return null;
    }

    // **************
    // Simple getters
    // **************

    /**
     * This is a description of all simple getters, for more details see the actual
     * getter methods.
     * Simple getters are shortcuts to get only the Model object without the status code.
     * These are usually used when more checks are done on the returned object that are not
     * covered by the actual checks in get methods.
     * Use of these should be as minimal as possible since they loose the information gained
     * from the more complex checks but at the moment there's a frequent use of these.
     *
     */
    // TODO: These should be removed and the returned status codes should be used to check operation success
    public static SavedDataField getSimpleSavedDataField(DataRow row, String key) {
        return getSimpleSavedDataField(row, key, null);
    }
    public static SavedDataField getSimpleSavedDataField(DataRow row, String key, Configuration config) {
        Pair<StatusCode, SavedDataField> pair = getSavedDataField(row, key, config);
        return pair.getRight();
    }
    public static SavedDataField getSimpleSavedDataField(RevisionData revision, String key) {
        return getSimpleSavedDataField(revision, key, null);
    }
    public static SavedDataField getSimpleSavedDataField(RevisionData revision, String key, Configuration config) {
        Pair<StatusCode, SavedDataField> pair = getSavedDataField(revision, key, config);
        return pair.getRight();
    }
    public static ContainerDataField getSimpleContainerDataField(RevisionData revision, String key) {
        return getSimpleContainerDataField(revision, key, null);
    }
    public static ContainerDataField getSimpleContainerDataField(RevisionData revision, String key, Configuration config) {
        Pair<StatusCode, ContainerDataField> pair = getContainerDataField(revision, key, config);
        return pair.getRight();
    }
    public static ReferenceContainerDataField getSimpleReferenceContainerDataField(RevisionData revision, String key) {
        return getSimpleReferenceContainerDataField(revision, key, null);
    }
    public static ReferenceContainerDataField getSimpleReferenceContainerDataField(RevisionData revision, String key, Configuration config) {
        Pair<StatusCode, ReferenceContainerDataField> pair = getReferenceContainerDataField(revision, key, config);
        return pair.getRight();
    }

    // ******************************************
    // General SavedDataField getters and setters
    // ******************************************

    /**
     * Returns requested field from given map as a savedDataField.
     * Passes request to getSavedDataField that accepts configuration and uses null for configuration value.
     *
     * @param fieldMap Map from where the field should be returned
     * @param key Field key of the requested field
     * @return See the implementation with configuration
     */
    public static Pair<StatusCode, SavedDataField> getSavedDataField(Map<String, DataField> fieldMap, String key) {
        return getSavedDataField(fieldMap, key, null);
    }

    /**
     * Sets provided value to provided DataField map with the provided key.
     * If no field is present previously in the map assumes that a SavedDataField should be set instead.
     * If non SavedDataField is present then halts operation.
     * Returns the set field so this can be used to create new fields also.
     *
     * @param fieldMap Map of DataFields where value should be inserted
     * @param key Field key
     * @param value Value to be inserted in field
     * @param time LocalDateTime for the saved value, can be null in which case a new time instance is created
     * @param changeMap Map of changes where insertion should be recorded
     * @return See the implementation with configuration
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(Map<String, DataField> fieldMap, String key, String value, LocalDateTime time, Map<String, Change> changeMap) {
        // Funnel through to actual implementation
        return setSavedDataField(fieldMap, key, value, time, changeMap, null);
    }

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
    public static Pair<StatusCode, SavedDataField> getSavedDataField(Map<String, DataField> fieldMap, String key, Configuration config) {
        if(fieldMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_CONTAINER);
        if(result != null) return new ImmutablePair<>(result, null);

        DataField field = fieldMap.get(key);
        result = fieldChecks(field, FieldCheck.NOT_NULL, FieldCheck.SAVED_DATA_FIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return new ImmutablePair<>(StatusCode.FIELD_FOUND, (SavedDataField)field);
    }

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
     * @param time LocalDateTime for the saved value, can be null in which case a new time instance is created
     * @param changeMap Map of changes where insertion should be recorded
     * @param config Configuration where given field key should be found
     * @return Tuple of StatusCode and SavedDataField. If status is FIELD_INSERT or FIELD_UPDATE then SavedDataField is the field that was set,
     *         if status is NO_CHANGE_IN_VALUE then SavedDataField is null if given value is empty otherwise SavedDataField is null
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(Map<String, DataField> fieldMap, String key, String value, LocalDateTime time, Map<String, Change> changeMap, Configuration config) {
        if(fieldMap == null || changeMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_CONTAINER);
        if(result != null) return new ImmutablePair<>(result, null);

        // If no time instance was provided then create a new one
        if(time == null) {
            time = new LocalDateTime();
        }

        DataField field = fieldMap.get(key);
        result = fieldChecks(field, FieldCheck.SAVED_DATA_FIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        // No value provided and no previous field exists, no new field needs to be created
        if(field == null && StringUtils.isEmpty(value)) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, null);
        }

        SavedDataField saved;
        // No previous field, create SavedDataField, set value and return
        if(field == null) {
            saved = SavedDataField.build(key)
                    .setValue(setSimpleValue(createSavedValue(time), value));
            fieldMap.put(key, saved);
            changeMap.put(key, new Change(key));
            return new ImmutablePair<>(StatusCode.FIELD_INSERT, saved);
        }

        // There was previous field, check for changes, if change present then set value and change
        saved = (SavedDataField)field;
        if((!saved.hasValue() && !StringUtils.isEmpty(value)) || !saved.valueEquals(value)) {
            saved.setValue(setSimpleValue(createSavedValue(time), value));
            changeMap.put(key, new Change(key));
            return new ImmutablePair<>(StatusCode.FIELD_UPDATE, saved);
        } else {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, saved);
        }
    }

    // ************************************************************
    // Getters and setters for SavedDataFields from and to revision
    // ************************************************************

    /**
     * Return SavedDataField with given field key from revision.
     *
     * @param revision RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> getSavedDataField(RevisionData revision, String key) {
        return getSavedDataField(revision, key, null);
    }

    /**
     * Sets a field with given key in revision to value provided.
     * If field does not exist then creates the field.
     * Sets change if new value differs from old value
     * Assumes that set field should be SavedDataField
     *
     * @param revision RevisionData to be modified
     * @param key Field key
     * @param value Value to be inserted in field
     * @param time LocalDateTime for the saved value
     * @return See setSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(RevisionData revision, String key, String value, LocalDateTime time) {
        return setSavedDataField(revision, key, value, time, null);
    }

    /**
     * Return SavedDataField with given field key from revision.
     * If field does not exist in configuration returns null
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is a CONTAINER field return null.
     *
     * Otherwise returns field from data typed as SavedDataField (assumes data is correct)
     * @param revision RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> getSavedDataField(RevisionData revision, String key, Configuration config) {
        if(revision == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getSavedDataField(revision.getFields(), key, config);
    }

    /**
     * Sets a field with given key in revision to value provided.
     * Checks the configuration to make sure that the field should be SavedDataField and that it can be
     * inserted here.
     *
     * @param revision RevisionData to be modified
     * @param key Field key
     * @param value Value to be inserted in field
     * @param time LocalDateTime for the saved value
     * @param config Configuration where given field key should be found
     * @return See setSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(RevisionData revision, String key, String value, LocalDateTime time, Configuration config) {
        if(revision == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return setSavedDataField(revision.getFields(), key, value, time, revision.getChanges(), config);
    }

    // ******************************
    // Getters for ContainerDataField
    // ******************************

    /**
     * Returns requested field as ContainerDataField
     * See getSavedDataField for more details
     *
     * @param fieldMap Map of DataFields
     * @param key Field key
     * @return See getSavedDataField for more details
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(Map<String, DataField> fieldMap, String key) {
        return getContainerDataField(fieldMap, key, null);
    }

    /**
     * Returns requested field as ContainerDataField.
     * See getSavedDataField for more details
     *
     * @param fieldMap Map of DataFields
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for more details
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(Map<String, DataField> fieldMap, String key, Configuration config) {
        if(fieldMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.TYPE_CONTAINER);
        if(result != null) return new ImmutablePair<StatusCode, ContainerDataField>(result, null);

        DataField field = fieldMap.get(key);
        result = fieldChecks(field, FieldCheck.NOT_NULL, FieldCheck.CONTAINER_DATA_FIELD);
        if(result != null) return new ImmutablePair<StatusCode, ContainerDataField>(result, null);

        return new ImmutablePair<>(StatusCode.FIELD_FOUND, (ContainerDataField)field);
    }

    // ********************************************
    // Getters for ContainerDataField from revision
    // ********************************************

    /**
     * Return requested field as ContainerDataField from revision.
     * See getSavedDataField for more details.
     *
     * @param revision RevisionData
     * @param key Field key
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(RevisionData revision, String key) {
        return getContainerDataField(revision, key, null);
    }

    /**
     * Return requested field as ContainerDataField from revision.
     * See getSavedDataField for more details.
     *
     * @param revision RevisionData
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for details.
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(RevisionData revision, String key, Configuration config) {
        if(revision == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getContainerDataField(revision.getFields(), key, config);
    }

    // ***************************************
    // Getters for ContainerDataField from row
    // ***************************************

    /**
     * Return requested field as ContainerDataField from row.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key Field key
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(DataRow row, String key) {
        return getContainerDataField(row, key, null);
    }

    /**
     * Return requested field as ContainerDataField from revision.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for details.
     */
    public static Pair<StatusCode, ContainerDataField> getContainerDataField(DataRow row, String key, Configuration config) {
        if(row == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.IS_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getContainerDataField(row.getFields(), key, config);
    }

    // ***************************************
    // Getters for ReferenceContainerDataField
    // ***************************************

    /**
     * Returns requested field as ReferenceContainerDataField
     * See getSavedDataField for more details
     *
     * @param fieldMap Map of DataFields
     * @param key Field key
     * @return See getSavedDataField for more details
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(Map<String, DataField> fieldMap, String key) {
        return getReferenceContainerDataField(fieldMap, key, null);
    }

    /**
     * Returns requested field as ContainerDataField.
     * See getSavedDataField for more details
     *
     * @param fieldMap Map of DataFields
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for more details
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(Map<String, DataField> fieldMap, String key, Configuration config) {
        if(fieldMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.TYPE_REFERENCECONTAINER);
        if(result != null) return new ImmutablePair<>(result, null);

        DataField field = fieldMap.get(key);
        result = fieldChecks(field, FieldCheck.NOT_NULL, FieldCheck.REFERENCE_CONTAINER_DATA_FIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return new ImmutablePair<>(StatusCode.FIELD_FOUND, (ReferenceContainerDataField)field);
    }

    // *****************************************************
    // Getters for ReferenceContainerDataField from revision
    // *****************************************************

    /**
     * Return requested field as ReferenceContainerDataField from revision.
     * See getSavedDataField for more details.
     *
     * @param revision RevisionData
     * @param key Field key
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(RevisionData revision, String key) {
        return getReferenceContainerDataField(revision, key, null);
    }

    /**
     * Return requested field as ContainerDataField from revision.
     * See getSavedDataField for more details.
     *
     * @param revision RevisionData
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for details.
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(RevisionData revision, String key, Configuration config) {
        if(revision == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.NOT_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getReferenceContainerDataField(revision.getFields(), key, config);
    }

    // ************************************************
    // Getters for ReferenceContainerDataField from row
    // ************************************************

    /**
     * Return requested field as ReferenceContainerDataField from row.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key Field key
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(DataRow row, String key) {
        return getReferenceContainerDataField(row, key, null);
    }

    /**
     * Return requested field as ContainerDataField from row.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for details.
     */
    public static Pair<StatusCode, ReferenceContainerDataField> getReferenceContainerDataField(DataRow row, String key, Configuration config) {
        if(row == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.IS_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getReferenceContainerDataField(row.getFields(), key, config);
    }

    // *********************************************
    // Getters and setters for SavedDataField in row
    // *********************************************

    /**
     * Returns requested field from given row as SavedDataField.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key FieldKey
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> getSavedDataField(DataRow row, String key) {
        return getSavedDataField(row, key, null);
    }

    /**
     * Set field in a row to given value and set change for that field if not yet present.
     * Field is assumed to be SavedDataField.
     * Ultimately funnels through general setSavedDataField implementation. Sets change containers if value changes and they were missing previously.
     *
     * @param row DataRow where the value should be set
     * @param key Field key of the field where value should be set
     * @param value Value to be set, can be null to empty a field
     * @param time LocalDateTime for the saved value, can be null in which case a new time instance is created
     * @param changeMap Map that should contain the change set for this row (for example the main changes map of a revision data)
     * @return See setSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(DataRow row, String key, String value, LocalDateTime time, Map<String, Change> changeMap) {
        // Funnel through to more complete implementation
        return setSavedDataField(row, key, value, time, changeMap, null);
    }

    /**
     * Return requested field as SavedDataField from provided row.
     * See getSavedDataField for more details.
     *
     * @param row DataRow
     * @param key Field key
     * @param config Configuration
     * @return See getSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> getSavedDataField(DataRow row, String key, Configuration config) {
        if(row == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.IS_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        return getSavedDataField(row.getFields(), key, config);
    }

    /**
     * Configuration checking providing version of setRowSavedValue.
     * If provided with configuration then checks if given field can be set with saved data field value.
     * Funnels through general setSavedDataField implementation but tries to make sure that no unnecessary changes get logged.
     *
     * @param row DataRow where the value should be set
     * @param key Field key of the field where value should be set
     * @param value Value to be set, can be null to empty a field
     * @param time LocalDateTime for the saved value, can be null in which case a new time instance is created
     * @param changeMap Map that should contain the change set for this row (for example the main changes map of a revision data)
     * @param config Configuration where given field key should be found
     * @return See setSavedDataField for details
     */
    public static Pair<StatusCode, SavedDataField> setSavedDataField(DataRow row, String key, String value, LocalDateTime time, Map<String, Change> changeMap, Configuration config) {
        if(row == null || changeMap == null || StringUtils.isEmpty(key)) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        // Do config check
        StatusCode result = configChecks(config, key, ConfigCheck.IS_SUBFIELD);
        if(result != null) return new ImmutablePair<>(result, null);

        ContainerChange containerChange = (ContainerChange)changeMap.get(row.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(row.getKey());
            //changeContainer.put(row.getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(row.getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(row.getRowId());
            containerChange.put(rowChange);
        }

        // Set value to row using general setSavedDataField implementation with configuration.
        // It doesn't matter if configuration is present here or not since the setSavedDataField will handle both situations equally
        Pair<StatusCode, SavedDataField> saved = setSavedDataField(row.getFields(), key, value, time, rowChange.getChanges(), config);
        if(saved.getRight() == null) {
            // Some problem with setting saved field, don't continue
            return saved;
        }

        // Old
        // If rowChange contains a change for the key then we can be sure that value has changed and we can put the changes to provided map if they are missing
        //if(rowChange.hasChange(key)) {

        // If there's a saved value (since we didn't return previously) and status type is not NO_CHANGE_IN_VALUE then there has been a change.
        // Update changes to reflect this
        if(saved.getLeft() != StatusCode.NO_CHANGE_IN_VALUE) {
            // As a shortcut we can just put the rowChange and containerChange to their respective maps.
            // If they were there previously then nothing changes, if not then they need to be there anyway.
            containerChange.put(rowChange);
            changeMap.put(containerChange.getKey(), containerChange);
        }
        return saved;
    }

    // ********************
    // Other Row operations
    // ********************

    /**
     * Marks a row for removal and adds a change if change is missing
     * @param row DataRow to be marked for removal
     * @param changeMap Map that should contain the change for container where given row is present
     */
    public static void removeRow(DataRow row, Map<String, Change> changeMap) {
        // Sanity check
        if(row == null || changeMap == null || row.isRemoved()) {
            // If row or changeMap are not present or if the row is already removed there's no point in continuing
            return;
        }

        row.setRemoved(true);
        ContainerChange containerChange = (ContainerChange)changeMap.get(row.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(row.getKey());
            changeMap.put(row.getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(row.getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(row.getRowId());
            containerChange.put(rowChange);
        }
    }

    /**
     * Marks a reference for removal and adds a change if change is missing.
     * @param reference SavedReference to be marked for removal
     * @param changeMap Map that should contain the change for container where given row is present
     */
    public static void removeReference(SavedReference reference, Map<String, Change> changeMap) {
        if(reference == null || changeMap == null || reference.isRemoved()) {
            return;
        }

        reference.setRemoved(true);
        ContainerChange containerChange = (ContainerChange)changeMap.get(reference.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(reference.getKey());
            changeMap.put(reference.getKey(), containerChange);
        }
        RowChange rowChange = containerChange.get(reference.getRowId());
        if(rowChange == null) {
            rowChange = new RowChange(reference.getRowId());
            containerChange.put(rowChange);
        }
    }

    /**
     * Searches through a list of rows for a row containing given value in a field with given id.
     *
     * @param rows List of rows to search through
     * @param key Field key of field where value should be found
     * @param value Value that is searched for
     * @return DataRow that contains given value in requested field, or null if not found.
     */
    public static DataRow findRowWithFieldValue(List<DataRow> rows, String key, String value) {
        for(DataRow row : rows) {
            Pair<StatusCode, SavedDataField> pair = getSavedDataField(row.getFields(), key);
            SavedDataField field = pair.getRight();
            if(field != null && field.hasValue() && field.valueEquals(value)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Searches through a list of references for a reference containing given value
     * @param references List of references to search through
     * @param value Reference value that is searched for
     * @return SavedReference matching given value or null if none found
     */
    public static SavedReference findReferenceWithValue(List<SavedReference> references, String value) {
        for(SavedReference reference : references) {
            if(reference.getActualValue().equals(value)) {
                return reference;
            }
        }
        return null;
    }

    /**
     * Uses findRowWithFieldValue to search for existing row in given rows list.
     * If row is not found creates a new row and inserts it to the list.
     * Since it can be assumed that it's desirable to find the field with the given value from the rows list
     * the field is created on the row with the given value
     *
     * @param revision Revision containing this row. Needed to generate new row id if required
     * @param container ContainerDataField where row should be or where new row is inserted
     * @param key Field key of the field where the value should be found
     * @param value Value that is searched for
     * @param changeMap Map where the container change containing this rows changes should reside
     * @param time Time for possible creation of row and field. Can be null
     * @return Tuple of StatusCode and DataRow. StatusCode tells if the returned row is a new insert or not
     */
    public static Pair<StatusCode, DataRow> findOrCreateRowWithFieldValue(RevisionData revision, ContainerDataField container, String key, String value, Map<String, Change> changeMap, LocalDateTime time) {
        DataRow row = findRowWithFieldValue(container.getRows(), key, value);
        StatusCode status;
        if(row == null) {
            row = new DataRow(container.getKey(), revision.getNewRowId());
            container.putRow(row);
            setSavedDataField(row, key, value, time, changeMap);
            status = StatusCode.NEW_ROW;
        } else {
            status = StatusCode.FOUND_ROW;
        }

        return new ImmutablePair<StatusCode, DataRow>(status, row);
    }
}
