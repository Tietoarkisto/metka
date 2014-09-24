// same as .url method, except also navigates to the url

define(function (require) {
    'use strict';
    return function (key, extend) {
        var metka = require('./../metka');
        return metka.contextPath + '/web' + (function () {
            if (key[0] === '/') {
                return key;
            } else {
                return '/' + {
                    approve: 'revision/ajax/approve',
                    compareRevisions: 'history/revisions/compare',
                    conf: '/revision/ajax/configuration/{PAGE}',
                    create: 'revision/ajax/create',
                    download: 'download/{id}/{no}',
                    edit: 'revision/ajax/edit',
                    expert: 'expert/',
                    fileEdit: 'file/save/{value}',
                    fileSave: 'file/save',
                    fileUpload: 'file/upload',
                    listRevisions: 'history/revisions/{id}',
                    next: 'next/{page}/{id}',
                    options: 'references/collectOptionsGroup',
                    prev: 'prev/{page}/{id}',
                    remove: 'remove/{page}/{type}/{id}',
                    save: 'revision/ajax/save',
                    searchPage: 'revision/search/{page}',
                    searchAjax: 'revision/ajax/search',
                    view: 'revision/view/{page}/{id}/{no}',
                    viewAjax: 'revision/ajax/view/{page}/{id}/{no}',
                    ddiexport: 'revision/ddi/export/{id}/{no}'
                }[key];
            }
        })().supplant($.extend({
            no: ''
        }, metka, extend));
    };
});
