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
    NOT_TIME               // Valid time should be parsable from value
}
