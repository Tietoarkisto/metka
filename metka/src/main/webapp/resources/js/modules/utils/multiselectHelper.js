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

define(function (require) {
    'use strict';

    /**
     * @param [variables] an array containing all the variables
     * @param {startMultiselect} node that was clicked 1st while holding shift
     * @param {endMultiselect} node that was clicked 2nd while holding shift
     *
     */
    return function (variables, startMultiselect, endMultiselect) {
        // Select all nodes left between the 1st and the 2nd clicks
        var startIndex = null;
        var endIndex = null;

        for (var i = 0; i < variables.length; i++) {

            // Find the clicked nodes from the variable array
            if (JSON.stringify(variables[i]) === JSON.stringify(startMultiselect)) {
                startIndex = i;
            }
            if (JSON.stringify(variables[i]) === JSON.stringify(endMultiselect)) {
                endIndex = i;
            }
            // Handle a selection that goes from down to up
            if (endIndex !== null && endIndex < startIndex) {
                var tmpIndex = startIndex;
                startIndex = endIndex;
                endIndex = tmpIndex;
            }
        }
        // Mark selected nodes active one by one
        if(startIndex !== null && endIndex !== null){
            for(var i = startIndex; i <= endIndex; i++){
                if(variables[i].active === true){
                    $.extend(variables[i], {active: false});
                } else {
                    $.extend(variables[i], {active: true});
                }
            }
        }
    };
});
