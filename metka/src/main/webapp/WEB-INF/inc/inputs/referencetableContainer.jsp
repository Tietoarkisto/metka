<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
Referencecontainer table.
Supported params:
    field - Field key of referencecontainer
    addButton - Should row add button be included
    dialogId - Id of the dialog used for handling row data. If this is included then listener is added for this dialog so row updates can be made.
    handler - Name of the handler object in MetkaJS.DialogHandlers that is responsible of processing this referencecontainer
--%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="handler" value="${empty param.handler ? 'generalReferenceHandler' : param.handler}" />
<div class="singleCellTitle"><spring:message code="${context}.field.${param.field}"/></div>

<form:hidden path="values['${param.field}']" />
<table id="${param.field}" class="dataTable autobuild" data-context="${context}" data-handler="${handler}">
    <thead>
        <tr>
        <c:if test="${configuration[context].fields[param.field].showReferenceKey == true}">
            <c:set var="reference" value="${configuration[context].fields[param.field].reference}" />
            <c:set var="target" value="${configuration[context].references[reference].target}" />
            <th><spring:message code="${target}.field.${configuration[target].idField}"/></th>
        </c:if>
        <c:forEach var="subfield" items="${configuration[context].fields[param.field].subfields}">
            <c:if test="${configuration[context].fields[subfield].summaryField == true}"><th><spring:message code="${context}.field.${subfield}"/></th></c:if>
        </c:forEach>
        <c:if test="${configuration[context].fields[param.field].showSaveInfo == true}">
            <th><spring:message code="general.saveInfo.savedAt"/></th>
            <th><spring:message code="general.saveInfo.savedBy"/></th>
        </c:if>
        </tr>
    </thead>
    <tbody />
</table>
<c:if test="${not empty param.dialogId}">
    <script>
        MetkaJS.EventManager.listen(MetkaJS.E.Event.DIALOG_EVENT, "${param.field}", "${param.dialogId}", function(notifier) {
            MetkaJS.EventManager.notify(MetkaJS.E.Event.REFERENCE_CONTAINER_CHANGE, {target: "${param.field}", id: notifier.id});
        });
    </script>
</c:if>
<c:if test="${empty param.addButton or param.addButton == true}">
    <div class="rightAlignCell"><input type="button" class="button" value="<spring:message code="general.table.add"/>"
                                       onclick="MetkaJS.DialogHandlers['${handler}'].show('${param.field}')"/></div>
</c:if>
