package fi.uta.fsd.metka.model.access.enums;

public enum ConfigCheck {
    IS_CONTAINER,           // Field has to be one of container types
    NOT_CONTAINER,          // Field must not be one of container types
    IS_SUBFIELD,            // Field must be a subfield
    NOT_SUBFIELD,           // Field must not be a subfield
    TYPE_CONTAINER,         // Field must be of type CONTAINER
    TYPE_REFERENCECONTAINER // Field must be of type REFERENCECONTAINER
}