package fi.uta.fsd.metka.enums;

public enum FieldError {
    NOT_UNIQUE,             // Field is not unique as it should be
    IMMUTABLE,              // Tried to change immutable field
    MISSING_ROWS,           // Client removed row content from  container instead of just marking them as removed
    MISSING_VALUE,          // Value was missing from where there should have been a value, usually a row in reference container transfer field
    ROW_NOT_FOUND,          // Row matching a Transfer row could not be found even if it should exist based on Transfer data
    NOT_EDITABLE            // Tried to change field that is not editable
}
