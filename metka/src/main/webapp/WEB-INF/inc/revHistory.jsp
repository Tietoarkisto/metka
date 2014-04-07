<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Includes everything needed to implement revision history viewing and comparison --%>
<%--    Initialization for revision history viewing and comparison. All of these things have to be included when this
functionality is required including a component with id: showRevisions. --%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<script>
    MetkaJS.L10N.put("general.revision.replace", "<spring:message code='general.revision.replace'/>");
    MetkaJS.L10N.put("general.revision.compare.title", "<spring:message code='general.revision.compare.title'/>");
    // Init page specific translations
    MetkaJS.L10N.put("${context}", "<spring:message code='${context}'/>");
    <c:forEach var="field" items="${configuration[context].fields}">MetkaJS.L10N.put("${context}.field.${field.key}", "<spring:message code='${context}.field.${field.key}'/>");
    </c:forEach>
</script>

<script src="${pageContext.request.contextPath}/js/custom/history.js"></script>
<%--    End of revision history component requirements. --%>
<jsp:include page="../dialogs/revisionHistoryDialog.jsp" />
<input type="button" id="showRevisions" class="button" value="<spring:message code='general.buttons.revisionHistory'/>" />