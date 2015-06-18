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

package fi.uta.fsd.metka.model.access.enums;

public enum StatusCode {
    FIELD_FOUND,                        // Field was found, it was successfully determined to be correct type of DataField and was returned as such
    FIELD_MISSING,                      // Field can be DataField of correct type but no field with the given key was found
    FIELD_UPDATE,                       // DataField of correct type was found, it can exist (not prohibited by configuration or existing field of different type) and was updated
    FIELD_INSERT,                       // DataField of correct type can exist (not prohibited by configuration) but no previous field was found. New field was created
    FIELD_CHANGED,                      // Indicates that either a field update or field insert took place
    NO_CHANGE_IN_VALUE,                 // Either DataField was found but the value remained the same or there was no previous field and the value was nothing to insert.
    CONFIG_FIELD_MISSING,               // There's no field configuration for the given key in the configuration provided
    CONFIG_FIELD_TYPE_MISMATCH,         // Configuration tells us that the field can not be of provided type
    CONFIG_FIELD_LEVEL_MISMATCH,        // Requested field was not at right level, either a top level field was requested and config says field is a subfield or the other way around
    FIELD_TYPE_MISMATCH,                // Found field did not pass instanceof check
    INCORRECT_PARAMETERS,               // Some of the parameters provided to the method were not sufficient or were in some way incorrect
    NO_ROW_WITH_ID,                     // Used to indicate that no row with given row id was found
    NO_ROW_WITH_VALUE,                  // Used to indicate that no row with given value was found
    ROW_FOUND,                          // Used to indicate that old row was found with the request
    ROW_INSERT,                         // Used to indicate that a new row was created with the request
    ROW_CHANGE,                         // Operation changed row in some way that modified changes map
    ROW_REMOVED,                        // Row was removed from a row container
    FIELD_NOT_EDITABLE,                 // Field is not editable, it cannot be edited by user
    FIELD_NOT_MUTABLE,                  // Field is not immutable, value cannot change once given
    FIELD_NOT_WRITABLE,                 // Field is not writable, it should not be written to revision data
    FIELD_NOT_TRANSLATABLE             // Field is not translatable but value was checked or tried to set for a non default language
}
