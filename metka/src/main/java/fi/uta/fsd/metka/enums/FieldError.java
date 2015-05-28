package fi.uta.fsd.metka.enums;

public enum FieldError {
    NOT_UNIQUE,             // Field is not unique as it should be
    IMMUTABLE,              // Tried to change immutable field
    MISSING_ROWS,           // Client removed row content from  container instead of just marking them as removed
    MISSING_VALUE,          // Value was missing from where there should have been a value, usually a row in reference container transfer field
    ROW_NOT_FOUND,          // Row matching a Transfer row could not be found even if it should exist based on Transfer data
    NOT_EDITABLE,           // Tried to change field that is not editable
    APPROVE_FAILED,         // Tells that approval of sub object (like study variable) failed
    NOT_TRANSLATABLE,       // Field that is not marked for translation contains translations in some form
    // These following errors pertain to different field types and are returned by Value.typeCheck method. Null or empty value is never checked against a type
    NOT_BOOLEAN,            // Value should be either 'true' or 'false
    NOT_INTEGER,            // Value should be parsable as Long without exception (integer is a description in this case, not an implementation)
    NOT_REAL,               // Value should be parsable as Double without exception
    NOT_DATE,               // Valid date should be parsable from value
    NOT_DATETIME,           // Valid datetime should be parsable from value
    NOT_TIME,               // Valid time should be parsable from value
    // These are file path specific errors mainly related to study attachment file path and agreement file path
    NO_FILE,                // File either doesn't exist or is not a file or directory based on Java IO status
    AUTOMATIC_CHANGE,       // Value was changed automatically due to implementation specifics
    WRONG_LOCATION,
    AUTOMATIC_CHANGE_FAILED_FILE_EXISTS, // We tried to move a file to a correct new location but there was already a file there
    FILE_EXISTS,            // There already exists a file in a location where we try to insert a new file
    MOVE_FAILED,            // We failed to move the new file to correct location
    IS_DIRECTORY           // File path points to a directory based on Java IO status. This is wrong when actual file is expected
}
