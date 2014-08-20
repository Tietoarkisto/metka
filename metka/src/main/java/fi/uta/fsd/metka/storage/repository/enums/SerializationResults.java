package fi.uta.fsd.metka.storage.repository.enums;

/**
 * Special enumerator for serialization results since these differ so much from common return results
 */
public enum SerializationResults {
    SERIALIZATION_SUCCESS,              // Mainly result from JSONUtil. Serialization of given resource was a success
    DESERIALIZATION_SUCCESS,            // Mainly result from JSONUtil. Deserialization of given resource was a success
    SERIALIZATION_FAILED,               // Mainly result from JSONUtil. Serialization of given resource was a failure
    DESERIALIZATION_FAILED             // Mainly result from JSONUtil. Deserialization of given resource was a failure

}
