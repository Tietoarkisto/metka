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
    return function (options, type, $input, lang) {
        var key = options.field.key;
        var dataFormat = {
            DATE: "YYYY-MM-DD",
            DATETIME: "YYYY-MM-DDThh:mm:ss.s",
            TIME: "hh:mm:ss.s"
        }[type];
        var setup = {
            DATE: {
                options: {
                    pickTime: false
                },
                icon: 'calendar'
            },
            TIME: {
                options: {
                    pickDate: false
                },
                icon: 'time'
            },
            DATETIME: {
                options: {},
                icon: 'calendar'
            }
        }[type];
        setup.options.format = require('./dateFormats')[type];
        setup.options.language = 'fi';

        var $datePicker = $('<div class="input-group date">')
            .append($input)
            .append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant(setup))
            .datetimepicker(setup.options)
            .if(require('./isFieldDisabled')(options, lang), function () {
                this.data('DateTimePicker').disable();
            })
            .me(function () {
                var date = require('./data')(options).getByLang(lang);
                if (date) {
                    this.data('DateTimePicker').setDate(date);
                }
            });
        function setValue(e) {
            require('./data')(options).setByLang(lang, moment(e.date).format(dataFormat));
        }

        this.append($datePicker)
            // FIXME: kun kenttä on tyhjä ja ikonia klikataan, arvo tulee heti näkyviin mutta dp.change event ei triggeroidu. mahdollisesti korjattu datetimepickerin päivityksissä?
            .on('dp.change', setValue)
            .on('dp.hide', setValue)
            .on('dp.error', function (e) {
                require('./data')(options).setByLang(lang, $input.val());
            });
        $input.change(function () {
            require('./data')(options).setByLang(lang, $input.val());
        });
    }
});