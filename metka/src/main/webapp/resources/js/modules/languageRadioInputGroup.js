define(function (require) {
    'use strict';

    return function (options, name, initialLang) {
        return $('<div class="btn-group btn-group-xs btn-group-translation-lang" data-toggle="buttons">')
            // 'change' event instead of 'click', because these are radio buttons styled as regular buttons
            .on('change', 'label > input', function () {
                options.$events.trigger('translationLangChanged', [$(this).val()]);
            })
            .append([{
                text: ' fi',
                code: 'DEFAULT'
            }, {
                text: 'en',
                code: 'EN'
            }, {
                text: 'sv',
                code: 'SV'
            }].map(function (o) {
                    var $input = $('<input type="radio">')
                        .attr('name', name)
                        .val(o.code);
                    var $label = $('<label class="btn btn-default">')
                        .append($input, ' ', o.text);
                    if (initialLang === o.code) {
                        $label.addClass('active');
                        $input.prop('checked', true);
                    }

                    return $label;
                }))
    };
});
