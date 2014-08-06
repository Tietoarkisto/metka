define(function (require) {
    'use strict';

    $('.navbar-form').submit(function () {
        require('./server')('/todo-get-study-by-id-url{id}', {
            id: $(this).find('input[type="text"]').val()
        }, {
            method: 'GET',
            success: function (data) {
                log('todo: navigate to study...');
            },
            error: function () {
                require('./assignUrl')('/expertSearch');
            }
        });
        return false;
    });
});