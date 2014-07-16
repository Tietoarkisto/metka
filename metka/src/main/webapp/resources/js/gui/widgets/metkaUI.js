(function () {
    'use strict';

    $.widget('metka.metkaUI', $.metka.metka, {
        _create: function () {
            console.log('create ui', this.options);
            //console.log(JSON.stringify(this.options.content, null, 4));
            this._super();

            if (MetkaJS.SingleObject.state !== 'DRAFT') {
                this.options.readOnly = true;
            }

            this.header();
            this.container();
            this.buttonContainer();
        },
        header: function () {
            this.element.append((function (header) {
                var $header = $('<div class="page-header">');


                var supplant = {
                    page: MetkaJS.Globals.page.toUpperCase()
                };

                if (!header) {
                    header = {
                        localized: 'type.{page}.title',
                        pattern: '{localized} - {id} - {revision}{state}',
                        buttons: $('<div class="pull-right normalText">')
                            .append((function () {
                                var buttonCreateFunctions = MetkaJS.SingleObject.state === 'DRAFT' ? [] : [function () {
                                    $(this)
                                        .addClass('btn-xs')
                                        .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                                        .click(function () {
                                            $.get(MetkaJS.url('listRevisions'), function (response) {
                                                response.map(function (row) {
                                                    return row.revision
                                                });
                                            });
                                            MetkaJS.assignUrl('prev');
                                        });
                                }, function () {
                                    $(this)
                                        .addClass('btn-xs')
                                        .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                                        .click(function () {
                                            MetkaJS.assignUrl('next');
                                        });
                                }];
                                buttonCreateFunctions.push(function () {
                                    $(this)
                                        .addClass('btn-xs')
                                        .text(MetkaJS.L10N.get('general.buttons.download'))
                                        .click(function () {
                                            MetkaJS.assignUrl('download');
                                        });
                                });
                                return buttonCreateFunctions;
                            })().map(function (create) {
                                return $.metka.metkaButton({
                                    create: create,
                                    style: 'default'
                                }).element;
                            }))
                    };
                    var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
                    supplant.id = labelAndValue({
                        label: MetkaJS.L10N.get('general.id'),
                        value: MetkaJS.SingleObject.id
                    });
                    supplant.revision = labelAndValue({
                        label: MetkaJS.L10N.get('general.revision'),
                        value: MetkaJS.SingleObject.revision
                    });
                    supplant.state = MetkaJS.SingleObject.state === 'DRAFT' ? ' - ' + MetkaJS.L10N.get('general.DRAFT') : '';
                }

                supplant.localized = MetkaJS.L10N.get(header.localized.supplant(supplant));
                $header.html(header.pattern.supplant(supplant));

                if (header.buttons) {
                    $header.append(header.buttons);
                }

                return $header;
            })(this.options.header));
        },
        buttonContainer: function () {
            var $buttons = $.metka.metkaButtonContainer(this.options).element;
            if ($buttons.children().length > 0) {
                this.element.append($buttons);
            }
        }
    });
})();
