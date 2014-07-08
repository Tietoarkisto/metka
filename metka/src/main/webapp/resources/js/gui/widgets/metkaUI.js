(function () {
    'use strict';

    $.widget('metka.metkaUI', $.metka.metka, {
        _create: function () {
            console.log('create ui', this.options);
            //console.log(JSON.stringify(this.options.content, null, 4));
            this._super();

            this.header();
            this.container();
            this.buttonContainer();
        },
        header: function () {
            var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');

            var header = '{page} - {id} - {revision}{state}'.supplant({
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
            });

            this.element.append($('<div class="pageTitle row">')
                .html(header)
                .append($('<div class="floatRight normalText">')
                    .append(['PREVIOUS', 'NEXT', 'DOWNLOAD'].map(function (type) {
                        return $.metka.metkaButton({
                            type: type,
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
