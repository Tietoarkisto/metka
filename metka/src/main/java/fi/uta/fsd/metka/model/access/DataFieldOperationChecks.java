package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;

final class DataFieldOperationChecks {
    // Private constructor to disable instantiation
    private DataFieldOperationChecks() {}

    /**
     * Different checks that can be done to field.
     * Notice that NULL status must be checked separately, all of the type checks only
     * proceed if the field is not null, otherwise they are skipped.
     */
    static enum FieldCheck {
        NOT_NULL,                       // Field must not be null
        SAVED_DATA_FIELD,               // If field is not null checks to see if it's an instance of SavedDataField
        CONTAINER_DATA_FIELD,           // If field is not null checks to see if it's an instance of ContainerDataField
        REFERENCE_CONTAINER_DATA_FIELD  // If field is not null checks to see if it's an instance of ReferenceContainerDataField
    }

    /**
     * Does configuration checking based on given parameters.
     * Returns appropriate error messages if fault is detected or null if everything is OK or the config is missing
     *
     * @param config Configuration where field should be found
     * @param key Field key of the field to be checked
     * @param checks All special conditions to check for this config and field.
     * @return StatusCode with the possible error in checking or null if no errors detected
     */
    static StatusCode configChecks(Configuration config, String key, ConfigCheck... checks) {
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

    static StatusCode fieldChecks(DataField field, FieldCheck... checks) {
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
}
