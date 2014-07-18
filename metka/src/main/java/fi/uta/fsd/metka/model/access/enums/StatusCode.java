package fi.uta.fsd.metka.model.access.enums;

public enum StatusCode {
    FIELD_FOUND,                        // Field was found, it was successfully determined to be SavedDataField and was returned as such
    FIELD_MISSING,                      // Field can be DataField of correct type but no field with the given key was found
    FIELD_UPDATE,                       // DataField of correct type was found, it can exist (not prohibited by configuration or existing field of different type) and was updated
    FIELD_INSERT,                       // DataField of correct type can exist (not prohibited by configuration) but no previous field was found. New field was created
    NO_CHANGE_IN_VALUE,                 // Either DataField was found but the value remained the same or there was no previous field and the value was nothing to insert.
    CONFIG_FIELD_MISSING,               // There's no field configuration for the given key in the configuration provided
    CONFIG_FIELD_TYPE_MISMATCH,         // Configuration tells us that the field can not be of provided type
    CONFIG_FIELD_LEVEL_MISMATCH,        // Requested field was not at right level, either a top level field was requested and config says field is a subfield or the other way around
    FIELD_TYPE_MISMATCH,                // Found field did not pass instanceof check
    INCORRECT_PARAMETERS,               // Some of the parameters provided to the method were not sufficient or were in some way incorrect
    FOUND_ROW,                          // Used to indicate that old row was used with the request
    NEW_ROW,                            // Used to indicate that a new row was created with the request
    FOUND_REFERENCE,                    // Used to indicate that old reference was used with the request
    NEW_REFERENCE,                      // Used to indicate that a new reference was created with the request
    FIELD_NOT_EDITABLE,                 // Field is not editable, it cannot be edited by user
    FIELD_NOT_MUTABLE,                  // Field is not immutable, value cannot change once given
    FIELD_NOT_WRITABLE                  // Field is not writable, it should not be written to revision data
}
