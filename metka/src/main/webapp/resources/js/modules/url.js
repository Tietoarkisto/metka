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
