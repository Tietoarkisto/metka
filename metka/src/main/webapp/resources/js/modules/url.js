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

// same as .url method, except also navigates to the url

define(function (require) {
    'use strict';

    /**
     * @param {string} key Either one of predefined keys listed in this function, or string starting with '/'.
     * @param {object} extend simple object with string keys and values, to replace `{key}` parts in path.
     * @return {string} Path starting with '/'.
     * @example url('/foo/{hi}', {bar: 'biz'}) --> /<metka.contextPath>/web/foo/biz
     * @example url('view', {bar: 'biz'}) --> /<metka.contextPath>/web/revision/view/<currentPage>/<currentID>/<currentRevisionNo>
     * @example url('view', {PAGE: 'foo'}) --> /<metka.contextPath>/web/revision/view/foo/<currentID>/<currentRevisionNo>
     */
    return function (key, extend) {
        var metka = require('./../metka');
        return metka.contextPath + '/web' + (function () {
            if (key[0] === '/') {
                return key;
            } else {
                return '/' + {
                    // Search
                    searchPage: 'revision/search/{PAGE}',
                    searchAjax: 'revision/ajax/search',
                    expert: 'expert/',

                    // Revision viewing
                    view: 'revision/view/{PAGE}/{id}/{no}',
                    viewAjax: 'revision/ajax/view/{PAGE}/{id}/{no}',
                    conf: 'revision/ajax/configuration/{PAGE}',
                    adjacent: 'revision/adjacent',
                    prev: 'prev/{PAGE}/{id}',

                    // Revision operations
                    create: 'revision/ajax/create',
                    edit: 'revision/ajax/edit',
                    approve: 'revision/ajax/approve',
                    save: 'revision/ajax/save',
                    remove: 'revision/ajax/remove',

                    // Revision history
                    compareRevisions: 'history/revisions/compare',
                    listRevisions: 'history/revisions/{id}',

                    // Misc calls
                    download: 'revision/download',
                    options: 'references/collectOptionsGroup',
                    optionsByPath: 'references/referencePathGroup'
                }[key];
            }
        })().supplant($.extend({
            no: ''
        }, metka, extend));
    };
});
