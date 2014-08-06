package fi.uta.fsd.metka.storage.repository.enums;

/**
 * This enumeration should contain all the different codes for return results that can be sent from within storage in response to requests.
 * ReturnResult reuse is recommended but not mandated so multiple result codes can mean basically the same thing.
 * Some methods can return a collection of results or they can use the results indirectly in some way.
 */
public enum ReturnResult {
    SERIALIZATION_SUCCESS,              // Mainly result from JSONUtil. Serialization of given resource was a success
    DESERIALIZATION_SUCCESS,            // Mainly result from JSONUtil. Deserialization of given resource was a success
    SERIALIZATION_FAILED,               // Mainly result from JSONUtil. Serialization of given resource was a failure
    DESERIALIZATION_FAILED,             // Mainly result from JSONUtil. Deserialization of given resource was a failure
    REVISION_CREATED,                   // Either new revisionable was created or new draft revision was created for revisionable, edit returns revision found if existing revision is used
    REVISION_FOUND,                     // Either RevisionEntity or RevisionData was requested and result was found successfully
    REVISION_NOT_FOUND,                 // Either RevisionEntity or RevisionData was requested but no result was found
    REVISION_CONTAINED_NO_DATA,         // RevisionData was requested but RevisionEntity didn't contain any data, this is a serious error
    REVISION_OF_INCORRECT_TYPE,         // RevisionEntity or RevisionData of a certain ConfigurationType was requested but the found revision was of a different type
    NO_REVISION_FOR_REVISIONABLE,       // Revisionable object didn't contain any revisions (either both revision number values were null or no revisions could be found from database), this is a serious error
    REVISIONABLE_FOUND,                 // Requested revisionable was found and returned
    REVISIONABLE_NOT_FOUND,             // Requested revisionable object couldn't be found. Either there's no revisionable for requested id, or the revisionable with the id is of different type than requested
    REVISIONABLE_OF_INCORRECT_TYPE,     // RevisionableEntity of certain ConfigurationType was requested but the found Revisionable was of a different type
    REVISIONABLE_NOT_CREATED,           // Revisionable creation failed due to some error
    CONFIGURATION_CONTAINED_NO_DATA,    // Entity was found but data was empty, this is a serious error
    CONFIGURATION_FOUND,                // Requested configuration (either data or gui) was found
    CONFIGURATION_NOT_FOUND,            // Configuration (either data or gui) was not found
    DATABASE_DISCREPANCY,               // Data in the database is in a state that causes errors, operations had to be stopped
    DATABASE_INSERT_FAILED,             // Insert or merge operation failed for some reason
    DATABASE_INSERT_SUCCESS,            // Insert or update operation was successful
    MISC_JSON_NOT_FOUND,                // Requested miscellaneous json file was either not found or was empty.
    MISC_JSON_FOUND,                    // Requested miscellaneous json file was found
    REVISION_UPDATE_SUCCESSFUL,         // RevisionData was serialized and merged successfully
    REVISION_NOT_A_DRAFT,               // Revision was not in a draft state when draft was expected
    NO_CHANGES_TO_SAVE,                 // Save was requested but there were no changes to save
    SAVE_SUCCESSFUL_WITH_ERRORS,        // Save was successful (changed data was updated to database) but there were field errors
    SAVE_SUCCESSFUL,                    // Save was successful (changed data was updated to database) and there were no field errors
    TYPE_NOT_VALID_CONFIGURATION_TYPE,  // Given type was not one of defined Configuration types
    APPROVE_SUCCESSFUL,                 // Approval of the requested revision was successful
    APPROVE_FAILED,                     // Approval of the requested revision failed, errors should be marked to their respective fields
    SEARCH_FAILED,                      // Performed search failed
    SEARCH_SUCCESS,                     // Search was performed successfully
    VIEW_SUCCESSFUL,                    // Some data was requested for viewing, data was gathered successfully
    INCORRECT_TYPE_FOR_OPERATION,       // Operation was requested for a configuration type that is not handled by that operation
    ALL_PARAMETERS_FOUND,               // All required parameters were provided
    PARAMETERS_MISSING                 // Some parameter that is required is instead missing
}