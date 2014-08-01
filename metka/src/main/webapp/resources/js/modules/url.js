// same as .url method, except also navigates to the url

define(function (require) {
    'use strict';
    return function (key, extend) {
        var metka = require('./../metka');
        return metka.contextPath + (function () {
            if (key[0] === '/') {
                return key;
            } else {
                return '/' + {
                    approve: '{page}/ajax/approve',
                    compareRevisions: 'history/revisions/compare',
                    download: 'download/{id}/{revision}',
                    edit: '{page}/edit/{id}',
                    expertSearch: 'expertSearch/',
                    fileEdit: 'file/save/{value}',
                    fileSave: 'file/save',
                    fileUpload: 'file/upload',
                    listRevisions: 'history/revisions/{id}',
                    next: 'next/{page}/{id}',
                    options: 'references/collectOptionsGroup',
                    prev: 'prev/{page}/{id}',
                    remove: 'remove/{page}/{type}/{id}',
                    save: '{page}/ajax/save',
                    search: '{page}/ajax/search',
                    seriesAdd: 'series/add',
                    view: '{page}/view/{id}/{revision}'
                }[key];
            }
        })().supplant($.extend({
            id: metka.id,
            page: metka.page,
            revision: metka.revision
        }, extend));
    };
});
