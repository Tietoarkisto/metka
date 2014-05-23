(function() {
    'use strict';
    GUI.Fields.container = function ($elem, cell, disabled, context) {
        var columns = [];
        $elem.append($('<div class="form-group">')
            .append($('<div class="panel panel-primary">')
                .append($('<div class="panel-heading">')
                    .text(cell.title))
                .append($('<table class="table">')
                    .append($('<thead>')
                        .append($('<tr>')
                            .eachTo(MetkaJS.JSConfig[context].fields[cell.field.key].subfields, function (i, field) {
                                if (MetkaJS.JSConfig[context].fields[field].summaryField) {
                                    columns.push(field);
                                    this
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get(context + '.field.' + field)));
                                }
                            })
                            .eachTo(cell.field.columnFields, function (i, field) {
                                columns.push(field);
                                this
                                    .append($('<th>')
                                        .text(MetkaJS.L10N.get(context + '.field.' + field)));
                            })
                            .if(cell.field.showSaveInfo, function () {
                                columns.push('savedAt');
                                columns.push('savedBt');
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
            .if(!disabled, function () {
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
    }
}());