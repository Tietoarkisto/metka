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

            //this.element.metkaField(this.options.field);
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
            var id = GUI.id();
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
                    // TODO: get context
                    $input.metkaOptions(this.options);
                    //GUI.Fields.input.select($input, cell.field.key);
                } else {
                    $input.val(MetkaJS.Data.get(this.options.field.key));
                }
                this.element.append($input.prop('disabled', this.isFieldDisabled()));
            }
        }
    });

    $.widget('metka.metkaLabel', $.metka.metka, {
        defaultElement: '<label>',
        _create: function () {
            this.element
                .text(MetkaJS.L10N.localize(this.options, 'title'));
            if (this.options.required) {
                this.element.append('<span class="glyphicon glyphicon-asterisk"></span>');
            }
        }
    });

    $.widget('metka.metkaInput', $.metka.metka, {
        _create: function () {
            this.element
                .toggleClass('alert-warning', this.options.important);
        }
    });
})();