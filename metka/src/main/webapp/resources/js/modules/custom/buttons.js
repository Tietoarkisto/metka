define(function (require) {
    'use strict';

    return {
        exportDDI: require('./buttons/exportDDI'),
        importDDI: require('./buttons/importDDI'),
        studyAttachmentEdit: require('./buttons/studyAttachmentEdit'),
        studyVariablesClaim: require('./buttons/studyVariablesClaim'),
        studyVariablesEdit: require('./buttons/studyVariablesEdit'),
        studyVariablesRestore: require('./buttons/studyVariablesRestore'),
        studyVariablesSave: require('./buttons/studyVariablesSave')
    };
});
