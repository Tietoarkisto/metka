define(function (require) {
    'use strict';

    return function (text, children, transferRow) {
        return {
            text: text,
            children: children,
            appendVar: function (transferRow_var) {
                if (!transferRow.fields.vargroupvars) {
                    transferRow.fields.vargroupvars = {
                        key: 'vargroupvars',
                        rows: {
                            DEFAULT: []
                        },
                        type: 'REFERENCECONTAINER'
                    };
                }
                transferRow.fields.vargroupvars.rows.DEFAULT.push(transferRow_var);
            },
            transferRow: transferRow
        };
    };
});