define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            create: function (options) {
                var $elem = this;

                $.extend(true, options,
                    {
                        allowChange: function(params) {
                            if(!!params.current && params.current != params.change) {
                                require("./../../modal")({
                                    title: "Aineiston tyypin vaihto",
                                    body: "Aineiston tyypin vaihto saattaa aiheuttaa muutoksia aineistodataan. Haluatko varmasti vaihtaa tyyppiä?",
                                    buttons: [
                                        {
                                            type: "YES",
                                            create: function() {
                                                this.click(function() {
                                                    params.performChange(params.change);
                                                })
                                            }
                                        },
                                        {
                                            type: "NO",
                                            create: function() {
                                                this.click(function() {
                                                    params.reverseChange();
                                                })
                                            }
                                        }
                                    ]
                                })
                            } else {
                                params.performChange(params.change);
                            }
                        }
                    }
                );
            }
        }
    };
});
