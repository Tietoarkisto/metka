define(function (require) {
    'use strict';

    function isHandler(options) {
        return options.data.state.handler === MetkaJS.User.userName;
    }

    var buttons = {
        APPROVE: function (options) {
            this
                .click(require('./formAction')('approve')(options, function (response) {
                    $.extend(options.data, response.data);
                    $(this).trigger('refresh.metka');
                }, [
                    'APPROVE_SUCCESSFUL',
                    'APPROVE_FAILED_DURING_VALIDATION'
                ]));
        },
        CANCEL: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.cancel');
        },
        CLAIM: function (options) {
            this
                .click(function () {
                    var $this = $(this);
                    require('./server')('/revision/ajax/claim', {
                        data: JSON.stringify(options.data.key),
                        success: function (response) {
                            $.extend(options.data, response.data);
                            $this.trigger('refresh.metka');
                        }
                    });
                });
        },
        COMPARE: function (options) {
            options.title = MetkaJS.L10N.get('general.revision.compare');
            this.prop('disabled', true);
        },
        CUSTOM: function(options) {
            require(['./custom/buttons/'+options.customHandler], function(customHandler) {
                switch (typeof customHandler) {
                    case 'object':
                        $.extend(true, options, customHandler);
                        break;
                    case 'function':
                        customHandler.call(this, options);
                        break;
                }
            }.bind(this));
        },
        DISMISS: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.close');
        },
        EDIT: function (options) {
            this.click(require('./formAction')('edit')(options, function (response) {
                $.extend(options.data, response.data);
                $(this).trigger('refresh.metka');
                history.replaceState(undefined, '', require('./url')('view'));
            }, [
                'REVISION_FOUND',
                'REVISION_CREATED'
            ]));
        },
        EXPORT_DDI: function(options) {
            this.click( function() {
                require('./assignUrl')('ddiexport')
            });
        },
        HISTORY: function () {
            var metka = require('./../metka');
            this
                .click(function () {
                    function checkRadioGroups() {
                        var beginVal = $('input[name="beginGrp"]:checked').val();
                        var endVal = $('input[name="endGrp"]:checked').val();
                        if (beginVal) {
                            $('input[name="endGrp"]').each(function () {
                                if ($(this).val() <= beginVal) {
                                    $(this).attr('disabled', true);
                                } else {
                                    $(this).attr('disabled', false);
                                }
                            });
                        }
                        if (endVal) {
                            $('input[name="beginGrp"]').each(function () {
                                if ($(this).val() >= endVal) {
                                    $(this).attr('disabled', true);
                                } else {
                                    $(this).attr('disabled', false);
                                }
                            });
                        }
                        if (typeof beginVal !== 'undefined' && typeof endVal !== 'undefined') {
                            if (beginVal >= endVal) {
                                $('#compareRevisions').prop('disabled', true);
                            } else {
                                $('#compareRevisions').prop('disabled', false);
                            }
                        }
                    }

                    var $table = $('<table class="table">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .append((function () {
                                    var arr = [
                                        'general.revision',
                                        'general.revision.publishDate',
                                        'general.revision.compare.begin',
                                        'general.revision.compare.end'
                                    ];
/*
                                    if (metka.state === 'DRAFT') {
                                        arr.push('general.revision.replace');
                                    }*/

                                    return arr.map(function (entry) {
                                        return $('<th>')
                                            .text(MetkaJS.L10N.get(entry));
                                    });
                                })())));

                    require('./modal')({
                        title: MetkaJS.L10N.get('general.revision.revisions'),
                        body: $table,
                        buttons: [{
                            type: 'COMPARE',
                            //preventDismiss: true,
                            create: function () {
                                this
                                    .attr('id', 'compareRevisions')
                                    .click(function () {
                                        var $table = $('<table class="table">')
                                            .append($('<thead>')
                                                .append($('<tr>')
                                                    .append([
                                                            'Polku',
                                                            //'Kieli',
                                                            'Alkuperäinen arvo',
                                                            'Nykyinen arvo'
                                                        ].map(function (entry) {
                                                            return $('<th>')
                                                                .text(entry);
                                                        }))));
                                        require('./modal')({
                                            title: MetkaJS.L10N.get('general.revision.revisions'),
                                            body: $table,
                                            buttons: [{
                                                type: 'DISMISS'
                                            }]
                                        });

                                        require('./server')('/revision/revisionCompare', {
                                            data: JSON.stringify({
                                                id: metka.id,
                                                begin: $('input[name="beginGrp"]:checked').val(),
                                                end: $('input[name="endGrp"]:checked').val()
                                            }),
                                            success: function (response) {
                                                if (response.result === 'OPERATION_SUCCESSFUL') {
                                                    $table
                                                        .append($('<tbody>')
                                                            .append(response.rows.map(function (row) {
                                                                var parts = row.key.split('[');
                                                                if (parts.length < 2) {
                                                                    return;
                                                                };
                                                                return $('<tr>')
                                                                    .append([
                                                                        // TODO: get field title (titles are all over GUI conf, which is a problem)
                                                                        //parts[0],
                                                                        // TODO: get language from
                                                                        //parts[1].substr(0, parts[1].length - 1),
                                                                        row.key,
                                                                        row.original,
                                                                        row.current
                                                                    ].map(function (entry) {
                                                                        return $('<td>')
                                                                            .text(entry);
                                                                    }));
                                                            })));
                                                }
                                            }
                                        });
                                    });

                            }
                        }, {
                            type: 'DISMISS'
                        }]
                    });
                    require('./server')('/revision/revisionHistory', {
                        data: JSON.stringify({
                            id: metka.id
                        }),
                        success: function (response) {
                            $table
                                .append($('<tbody>')
                                    .append(response.rows.map(function (row) {
                                        return $('<tr>')
                                            .append((function () {
                                                var items = [
                                                    $('<a>', {
                                                        href: require('./url')('view', row),
                                                        text: row.no
                                                    }),
                                                        row.state === 'DRAFT' ? MetkaJS.L10N.get('general.DRAFT') : row.state,
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'beginGrp',
                                                        value: row.no,
                                                        change: checkRadioGroups
                                                    }),
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'endGrp',
                                                        value: row.no,
                                                        change: checkRadioGroups
                                                    })
                                                ];

                                                /*if (metka.state === 'DRAFT') {
                                                    items.push(require('./button')()()
                                                        .addClass('btn-xs')
                                                        .prop('disabled', true)
                                                        .text(MetkaJS.L10N.get('general.revision.replace')));
                                                }*/

                                                return items.map(function (entry) {
                                                    return $('<td>')
                                                        .append(entry);
                                                });
                                            })());
                                    })));

                            checkRadioGroups();
                        }
                    });
                });
        },
        NO: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.no');
        },
        REMOVE: function (options) {
            this.click(require('./remove')(options));
        },
        RELEASE: function (options) {
            this
                .click(function () {
                    var $this = $(this);
                    require('./server')('/revision/ajax/release', {
                        data: JSON.stringify(options.data.key),
                        success: function (response) {
                            $.extend(options.data, response.data);
                            $this.trigger('refresh.metka');
                        }
                    });
                });
        },
        RESTORE: function (options) {
            this
                .click(function () {
                    var $this = $(this);
                    var request = $.extend({
                        data: JSON.stringify(options.data.key),
                        success: function (response) {
                            $.extend(options.data, response.data);
                            $this.trigger('refresh.metka');
                        }
                    }, options.request);
                    require('./server')('/revision/ajax/restore', request);
                });
        },
        SAVE: function (options) {
            this
                .click(require('./save')(options, function (response) {
                    $.extend(options.data, response.data);
                    $(this).trigger('refresh.metka');
                }));
        },
        YES: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.yes');
        }
    };

    return require('./inherit')(function (options) {
        function isVisible() {
            if(options.hide) {
                return false;
            }
            if (options.data && options.data.state) {
                if (options.data.state.uiState === 'DRAFT' && options.hasOwnProperty('hasHandler') && options.hasHandler !== null) {
                    if (!!options.hasHandler !== !!options.data.state.handler) {
                        return false;
                    }
                }
                if (options.data.state.uiState === 'DRAFT' && options.hasOwnProperty('isHandler') && options.isHandler !== null) {
                    if (options.isHandler !== (options.data.state.handler === MetkaJS.User.userName)) {
                        return false;
                    }
                }
                if (options.states && options.states.length) {
                    // if every state mismatch
                    if (options.states.every(function (state) {
                        return options.data.state.uiState !== state;
                    })) {
                        return false;
                    }
                }
            }

            return require('./hasEveryPermission')(options.permissions);
        }

        options = options || {};

        var $button = $('<button type="button" class="btn">');

        if (options.type && buttons[options.type]) {
            buttons[options.type].call($button, options);
        }

        $button
            .addClass('btn-' + (options.style || 'primary'))
            .toggleClass('hiddenByButtonConfiguration', !isVisible());

        $button
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (options.create) {
            options.create.call($button, options);
        }

        return $button;
    });
});
