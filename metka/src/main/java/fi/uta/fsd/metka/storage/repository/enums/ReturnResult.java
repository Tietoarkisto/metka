/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.repository.enums;

/**
 * This enumeration should contain all the different codes for return results that can be sent from within storage in response to requests.
 * ReturnResult reuse is recommended but not mandated so multiple result codes can mean basically the same thing.
 * Some methods can return a collection of results or they can use the results indirectly in some way.
 */
public enum ReturnResult {
    REVISION_CREATED,                   // Either new revisionable was created or new draft revision was created for revisionable, edit returns revision found if existing revision is used
    REVISION_FOUND,                     // Either RevisionEntity or RevisionData was requested and result was found successfully
    REVISION_NOT_FOUND,                 // Either RevisionEntity or RevisionData was requested but no result was found
    REVISION_CONTAINED_NO_DATA,         // RevisionData was requested but RevisionEntity didn't contain any data, this is a serious error
    REVISION_OF_INCORRECT_TYPE,         // RevisionEntity or RevisionData of a certain ConfigurationType was requested but the found revision was of a different type
    REVISION_NOT_VALID,                 // There's something wrong with revision
    NO_REVISION_FOR_REVISIONABLE,       // Revisionable object didn't contain any revisions (either both revision number values were null or no revisions could be found from database), this is a serious error
    REVISIONABLE_FOUND,                 // Requested revisionable was found and returned
    REVISIONABLE_NOT_FOUND,             // Requested revisionable object couldn't be found. Either there's no revisionable for requested id, or the revisionable with the id is of different type than requested
    REVISIONABLE_OF_INCORRECT_TYPE,     // RevisionableEntity of certain ConfigurationType was requested but the found Revisionable was of a different type
    REVISIONABLE_NOT_CREATED,           // Revisionable creation failed due to some error
    REVISIONABLE_NOT_REMOVED,           // Revisionable was not marked as removed when it was expected to be
    REVISIONABLE_REMOVED,               // Revisionable was marked as removed and changes can't be made
    CONFIGURATION_CONTAINED_NO_DATA,    // Entity was found but data was empty, this is a serious error
    CONFIGURATION_FOUND,                // Requested configuration (either data or gui) was found
    CONFIGURATION_NOT_FOUND,            // Configuration (either data or gui) was not found
    CONFIGURATION_NOT_VALID,            // Configuration had problems either during serialization or deserialization
    DATABASE_DISCREPANCY,               // Data in the database is in a state that causes errors, operations had to be stopped
    DATABASE_INSERT_FAILED,             // Insert or merge operation failed for some reason
    DATABASE_INSERT_SUCCESS,            // Insert or update operation was successful
    MISC_JSON_NOT_FOUND,                // Requested miscellaneous json file was either not found or was empty.
    MISC_JSON_FOUND,                    // Requested miscellaneous json file was found
    REVISION_UPDATE_SUCCESSFUL,         // RevisionData was serialized and merged successfully
    REVISION_NOT_A_DRAFT,               // Revision was not in a draft state when draft was expected or parent was not a draft when child requested edit
    OPERATION_SUCCESSFUL_WITH_ERRORS,   // Operation was successful (changed data was updated to database) but there were field errors
    TYPE_NOT_VALID_CONFIGURATION_TYPE,  // Given type was not one of defined Configuration types
    SEARCH_FAILED,                      // Performed search failed
    VIEW_SUCCESSFUL,                    // Some data was requested for viewing, data was gathered successfully
    INCORRECT_TYPE_FOR_OPERATION,       // Operation was requested for a configuration type that is not handled by that operation
    ALL_PARAMETERS_FOUND,               // All required parameters were provided
    PARAMETERS_MISSING,                 // Some parameter that is required is instead missing
    USER_NOT_HANDLER,                   // User is not the current handler of a given revision
    CAN_CREATE_DRAFT,                   // User is allowed to create a new draft revision of object
    REVISION_NOT_CREATE,                // Some reason stopped revision creation for new draft revision
    EXCEPTION,                          // Unhandled exception happened at some point
    OPERATION_SUCCESSFUL,               // General success result for operations
    OPERATION_FAIL,                     // General fail result for operations
    NO_RESULTS,                         // General no results response for operations that request data that is not found but there is no error
    WRONG_USER,                         // User with the wrong user name tried to perform operation that is restricted to one user at a time (e.g. saving a revision)
    WRONG_ROLES,                        // Role(s) the user has don't allow them to perform the requested operation
    REFERENCE_FOUND,                    // Requested reference information was found
    REFERENCE_MISSING,                  // Requested reference information was not found
    EMPTY_QUERY,                        // Provided query was empty and so it could not be parsed
    MALFORMED_LANGUAGE,                 // Provided query language parameter is malformed
    MALFORMED_QUERY,                    // Search query (usually expert search) was in some way invalid and could not be parsed to actual query
    PAGE_CREATED,                       // Binder page was created
    PAGE_UPDATED,                       // Binder page was updated
    PAGE_REMOVED,                       // Binder page was removed successfully
    RESTRICTION_VALIDATION_FAILURE,     // Restriction validation encountered a failed validation
    CASCADE_FAILURE,                    // Operation was cascaded but one of more of the cascades fail and so the root operation fails
    API_AUTHENTICATION_FAILED,          // Authentication in API-method failed
    EMPTY_PATH,                         // Required path was empty
    MALFORMED_PATH,                     // Required path was malformed e.g. incomplete or didn't point to valid end
    EMPTY_SUBQUERY,                     // If subquery returns an empty list then the whole query is aborted. The Main query won't compile if subquery is empty
    NO_CHANGES,                         // Operation did not result in any changes
    HAS_HANDLER,                        // Revision already has handler in a situation where null handler is required
    EXCEPTION_DURING_API_CALL,
    FILE_ALREADY_EXISTS                // Special failure condition when file move is tried to a location that already contains a file
}