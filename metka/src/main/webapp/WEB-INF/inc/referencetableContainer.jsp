<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="handler" value="${empty param.handler ? 'generalReferenceHandler' : param.handler}" />
<div class="singleCellTitle"><spring:message code="${context}.field.${param.field}"/></div>

<form:hidden path="values['${param.field}']" />
<table id="${param.field}" class="dataTable autobuild" data-context="${context}" data-handler="${handler}">
    <thead>
    <tr>
        <c:if test="${configuration[context].fields[param.field].showReferenceKey == true}">
            <c:set var="reference" value="${configuration[context].fields[param.field].reference}" />
            <th><spring:message code="${configuration[context].references[reference].targetType}.field.${configuration[context].references[reference].valueField}"/></th>
        </c:if>
        <%--<c:forEach var="subfield" items="${configuration[context].fields[param.field].subfields}">
            <c:if test="${configuration[context].fields[subfield].summaryField == true}"><th><spring:message code="${context}.field.${subfield}"/></th></c:if>
        </c:forEach> TODO: Subfields for REFERENCECONTAINER--%>
        <c:if test="${configuration[context].fields[param.field].showSaveInfo == true}">
            <th><spring:message code="general.saveInfo.savedAt"/></th>
            <th><spring:message code="general.saveInfo.savedBy"/></th>
        </c:if>
    </tr>
    </thead>
    <tbody />
</table>
<c:if test="${empty param.addButton or param.addButton == true}">
    <div class="rightAlignCell"><input type="button" class="button" value="<spring:message code="general.table.add"/>"
                                       onclick="MetkaJS.DialogHandlers['${handler}'].show('${param.field}')"/></div>
</c:if>
