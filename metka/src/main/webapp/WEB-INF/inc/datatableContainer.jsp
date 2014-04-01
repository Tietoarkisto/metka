<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="handler" value="${empty param.handler ? 'generalContainerHandler' : param.handler}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="singleCellTitle"><spring:message code="${context}.field.${param.field}"/></div>

<form:hidden path="values['${param.field}']" />
<table id="${param.field}" class="dataTable autobuild" data-context="${context}" data-handler="${handler}">
    <thead>
        <tr>
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
<c:if test="${(empty param.addButton or param.addButton == true) and not readonly}">
    <div class="rightAlignCell"><input type="button" class="button" value="<spring:message code="general.table.add"/>"
            onclick="MetkaJS.DialogHandlers['${handler}'].show('${param.field}')"/></div>
</c:if>
<c:if test="${empty param.handler or param.handler == 'generalContainerHandler'}">
    <jsp:include page="../dialogs/generalContainerDialog.jsp">
        <jsp:param name="field" value="${param.field}" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>
</c:if>