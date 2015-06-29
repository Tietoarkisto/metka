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

    var resultParser = require('./resultParser');

    return {
        APPROVE: function (options) {
            this
                .click(require('./formAction')('approve')(options, function (response) {

                    if(resultParser(response.result).getResult() === 'NO_CHANGES') {
                        require('./assignUrl')('view', {no: ''});
                    } else {
                        $.extend(options.data, response.data);
                        options.$events.trigger('refresh.metka');
                    }
                }, [
                    'OPERATION_SUCCESSFUL',
                    'RESTRICTION_VALIDATION_FAILURE',
                    'NO_CHANGES'
                ], "approve"));
        },
        CANCEL: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.cancel');
        },
        CLAIM: function (options) {
            this
                .click(function () {
                    require('./server')('/revision/ajax/claim', {
                        data: JSON.stringify(options.data.key),
                        success: function (response) {
                            $.extend(options.data, response.data);
                            options.$events.trigger('refresh.metka');
                        }
                    });
                });
        },
        BEGIN_EDIT: function (options) {
            this
                .click(function () {
                    require('./server')('/revision/ajax/beginEdit', {
                        data: JSON.stringify(options.data.key),
                        success: function (response) {
                            if(resultParser(response.result).getResult() === 'REVISION_UPDATE_SUCCESSFUL') {
                                $.extend(options.data, response.data);
                                options.$events.trigger('refresh.metka');
                            } else {
                                require('./resultViewer')(response.result, 'beginEdit', function() {
                                    require('./assignUrl')('view');
                                });
                            }
                        }
                    });
                });
        },
        COMPARE: function (options) {
            options.title = MetkaJS.L10N.get('general.revision.compare');
            this.prop('disabled', true);
        },
        CUSTOM: function(options) {

        },
        DISMISS: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.close');
        },
        EDIT: function (options) {
            this.click(require('./formAction')('edit')(options, function (response) {
                $.extend(options.data, response.data);
                options.$events.trigger('refresh.metka');
                history.replaceState(undefined, '', require('./url')('view'));
            }, [
                'REVISION_FOUND',
                'REVISION_CREATED'
            ], "edit"));
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

                    require('./modal')($.extend(true, require('./optionsBase')(), {
                        title: MetkaJS.L10N.get('general.revision.revisions'),
                        body: $table,
                        large: true,
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
                                        require('./modal')($.extend(true, require('./optionsBase')(), {
                                            title: MetkaJS.L10N.get('general.revision.revisions'),
                                            body: $table,
                                            large: true,
                                            buttons: [{
                                                type: 'DISMISS'
                                            }]
                                        }));

                                        require('./server')('/revision/revisionCompare', {
                                            data: JSON.stringify({
                                                id: metka.id,
                                                begin: $('input[name="beginGrp"]:checked').val(),
                                                end: $('input[name="endGrp"]:checked').val()
                                            }),
                                            success: function (response) {
                                                if (resultParser(response.result).getResult() === 'OPERATION_SUCCESSFUL') {
                                                    $table
                                                        .append($('<tbody>')
                                                            .append(response.rows.map(function (row) {
                                                                var parts = row.key.split('[');
                                                                /*if (parts.length < 2) {
                                                                    return;
                                                                }*/
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
                    }));
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
                            options.$events.trigger('refresh.metka');
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
                            options.$events.trigger('refresh.metka');
                        }
                    }, options.request);
                    require('./server')('/revision/ajax/restore', request);
                });
        },
        SAVE: function (options) {
            this
                .click(require('./save')(options, function (response) {
                    $.extend(options.data, response.data);
                    options.$events.trigger('refresh.metka');
                }));
        },
        YES: function (options) {
            options.title = MetkaJS.L10N.get('general.buttons.yes');
        }
    };
});
