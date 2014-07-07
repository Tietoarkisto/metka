(function () {
    'use strict';

    $.widget('metka.metkaField', $.metka.metka, {
        _create: function () {
            //this.options.field.multichoice
            //this.options.field.showReferenceKey
            //this.options.field.showReferenceValue
            //this.options.field.handlerName
            var type = this.options.field.displayType || MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[this.options.field.key].type;

            if (type === 'CONTAINER') {
                this.containerField();
            } else {
                this.inputField(type);
            }
        },
        isFieldDisabled: function () {
            // TODO: disabled: MetkaJS.SingleObject.draft || (field.type === MetkaJS.E.Field.REFERENCE)
            return this.options.readOnly || !MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[this.options.field.key].editable;
        },
        containerField: function () {
            var columns = [];
            var context = MetkaJS.Globals.page.toUpperCase();
            this.element.append($('<div class="form-group">')
                .append($('<div class="panel panel-primary">')
                    .append($('<div class="panel-heading">')
                        .text(MetkaJS.L10N.localize(this.options, 'title')))
                    .append($('<table class="table">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .eachTo(MetkaJS.JSConfig[context].fields[this.options.field.key].subfields, function (i, field) {
                                    if (MetkaJS.JSConfig[context].fields[field].summaryField) {
                                        columns.push(field);
                                        this
                                            .append($('<th>')
                                                .text(MetkaJS.L10N.get(context + '.field.' + field)));
                                    }
                                })
                                .eachTo(this.options.field.columnFields, function (i, field) {
                                    columns.push(field);
                                    this
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get(context + '.field.' + field)));
                                })
                                .if(this.options.field.showSaveInfo, function () {
                                    columns.push('savedAt');
                                    columns.push('savedBy');
                                    this
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedAt')))
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedBy')));
                                })))
                        .append($('<tbody>')
                            .eachTo(['moo1', 'moo2'], function (i, data) {
                                this.append($('<tr>').eachTo(columns, function (i, column) {
                                    this.append($('<td>' + column + '-' + data + '</td>'));
                                }));
                            }))))
                .if(!this.isFieldDisabled(), function () {
                    this.append($('<div>') /*class="pull-right"*/
                        .append($('<button type="button" class="btn btn-primary">')
                            .text(MetkaJS.L10N.get('general.table.add'))
                            .click(function () {
                                // TODO: event handler
                                MetkaJS.DialogHandlers['${handler}'].show('${param.field}');
                            })));
                    /*
                     <c:if test="${empty param.handler or param.handler == 'generalContainerHandler'}">
                     <jsp:include page="../../dialogs/generalContainerDialog.jsp">
                     <jsp:param name="field" value="${param.field}" />
                     <jsp:param name="readonly" value="${readonly}" />
                     </jsp:include>
                     </c:if>*/
                }));
        },
        inputField: function (type) {
            var id = this.autoId();
            this.element.append($.metka.metkaLabel(this.options).element
                .attr('for', id));

            var options = {
                'class': 'form-control',
                id: id
            };
            var isSelection = type === 'SELECTION';
            var nodeType = (function () {
                if (isSelection) {
                    return 'select';
                }

                if (this.options.field.multiline) {
                    return 'textarea';
                }

                options.type = 'text';
                return 'input';
            }).apply(this);

            var $input = $('<' + nodeType + '>', options)
                .metkaInput(this.options);

            if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                this.datetime(type, $input);
            } else {
                if (isSelection) {
                    $input.metkaInput('select');
                } else {
                    var key = this.options.field.key;
                    $input
                        .val(MetkaJS.Data.get(key))
                        .change(function () {
                            MetkaJS.Data.set(key, $(this).val());
                        });
                }
                this.element.append($input.prop('disabled', this.isFieldDisabled()));
            }
        },
        datetime: function (type, $input) {
            'use strict';
            var setup = {
                DATE: {
                    options: {
                        pickTime: false,
                        format: 'YYYY-MM-DD'
                    },
                    icon: 'calendar'
                },
                TIME: {
                    options: {
                        pickDate: false,
                        format: 'hh.mm'
                    },
                    icon: 'time'
                },
                DATETIME: {
                    options: {
                        format: 'YYYY-MM-DD hh.mm'
                    },
                    icon: 'calendar'
                }
            }[type];
            setup.options.language = 'fi';

            try {
                var defaultDate = MetkaJS.Data.get(this.options.field.key);
                if (defaultDate) {
                    setup.options.defaultDate = defaultDate;
                }
            } catch (e) {}

            this.element.append($('<div class="input-group date">')
                .append($input)
                //.append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant({icon: icon[type]}))
                .append('<span class="input-group-addon"><span class="glyphicon glyphicon-' + setup.icon + '"></span>')
                .datetimepicker(setup.options)
                .if(this.isFieldDisabled(), function () {
                    this.data('DateTimePicker').disable();
                }));
        }
    });
})();