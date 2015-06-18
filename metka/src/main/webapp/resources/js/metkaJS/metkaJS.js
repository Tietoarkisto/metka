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

(function () {
	'use strict';

    /* Define MetkaJS namespace. Includes general global variables, objects, handlers and functions related to Metka-client.
     */
    window.MetkaJS = {
        User: {
            userName: null,
            displayName: null,
            role: null
        },
        L10N: null,
        // Globals-object contains global variables and sequences

        /**
         * Checks the existence of given variable.
         * Used to lessen repetitive coding and unifies checking.
         *
         * @param variable Variable to be checked for existence
         * @return {boolean} True if variable exists and false if not
         */
        exists: function (variable) {
            if (variable === null) {
                return false;
            }
            if (typeof variable === 'undefined') {
                return false;
            }

            // If all checks pass return true
            return true;
        },

        /**
         * Checks if given variable is a non-empty string.
         * Uses MetkaJS.exists() function as part of its implementation.
         *
         * @param variable Variable to be checked
         * @returns {boolean} True if given variable is a non-empty string, false otherwise
         */
        isString: function(variable) {
            if(!MetkaJS.exists(variable)) {
                return false;
            }

            if(typeof variable !== 'string') {
                return false;
            }

            if(variable.length <= 0) {
                return false;
            }

            return true;
        }
    };
})();
