package fi.uta.fsd.metka.enums;

public enum TransferFieldType {
    VALUE,                  // Field is a value field and so contains no rows and instead contains a TransferValue object.
    CONTAINER,              // Field is a container and so can have rows which each can have TransferFields. Contains no value itself.
    REFERENCECONTAINER      // Field is a reference container and so can have rows which each contain a value. Contains no value itself.
}
