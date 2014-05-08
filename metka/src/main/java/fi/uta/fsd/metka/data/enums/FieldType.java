package fi.uta.fsd.metka.data.enums;

/**
 * Enumerator for Configuration Field types.
 * Used to validate that type given in configuration file is a valid type.
 */
public enum FieldType {
    STRING,
    INTEGER,
    REAL,
    BOOLEAN,
    REFERENCE,
    CONTAINER,
    REFERENCECONTAINER,
    SELECTION,
    CONCAT,
    DATE,
    DATETIME,
    TIME
    // Add more as needed
}
