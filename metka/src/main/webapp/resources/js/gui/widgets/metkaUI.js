(function () {
    'use strict';

    $.widget('metka.metkaUI', $.metka.metka, {
        _create: function () {
            console.log('create ui', this.options);
            //console.log(JSON.stringify(this.options.content, null, 4));
            this._super();

            if (!MetkaJS.SingleObject.draft) {
                this.options.readOnly = true;
            }

            this.header();
            this.container();
            this.buttonContainer();
        },
        header: function () {
            var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');

            this.element.append($('<div class="pageTitle row">')
                .html('{page} - {id} - {revision}{state}'.supplant({
                    page: MetkaJS.L10N.get('type.{page}.title'.supplant({page: MetkaJS.Globals.page.toUpperCase()})),
                    id: labelAndValue({
                        label: MetkaJS.L10N.get('general.id'),
                        value: MetkaJS.SingleObject.id
                    }),
                    revision: labelAndValue({
                        label: MetkaJS.L10N.get('general.revision'),
                        value: MetkaJS.SingleObject.revision
                    }),
                    state: MetkaJS.SingleObject.draft ? ' - ' + MetkaJS.L10N.get('general.DRAFT') : ''
                }))
                .append($('<div class="floatRight normalText">')
                    .append([function () {
                        $(this)
                            .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                            .click(function () {
                                MetkaJS.SingleObject.adjacent(false);
                            });
                    }, function () {
                        $(this)
                            .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                            .click(function () {
                                MetkaJS.SingleObject.adjacent(true);
                            });
                    }, function () {
                        $(this)
                            .text(MetkaJS.L10N.get('general.buttons.download'))
                            .click(function () {
                                MetkaJS.PathBuilder().add('download').add(MetkaJS.SingleObject.id).add(MetkaJS.SingleObject.revision).navigate();
                            });
                    }].map(function (create) {
                        return $.metka.metkaButton({
                            create: create,
                            style: 'default'
                        }).element;
                    }))));
        },
        buttonContainer: function () {
            var $buttons = $.metka.metkaButtonContainer(this.options).element;
            if ($buttons.children().length > 0) {
                this.element.append($buttons);
            }
        }
    });
})();
