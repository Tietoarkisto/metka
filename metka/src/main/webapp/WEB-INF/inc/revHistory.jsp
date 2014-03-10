<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Includes everything needed to implement revision history viewing and comparison --%>
<%-- Required params:
        isDraft - Knowledge of is the object being viewed a draft
<%--    Initialization for revision history viewing and comparison. All of these things have to be included when this
functionality is required including a component with id: showRevisions. --%>
<script>
    MetkaGlobals.strings["general.revision.replace"] = "<spring:message code='general.revision.replace'/>";
    MetkaGlobals.strings["general.revision.compare.title"] = "<spring:message code='general.revision.compare.title'/>";
    // Init page specific translations
    MetkaGlobals.strings["${fn:toUpperCase(page)}"] = "<spring:message code='${fn:toUpperCase(page)}'/>";
    <c:forEach var="field" items="${configuration.fields}">MetkaGlobals.strings["${fn:toUpperCase(page)}.field.${field.key}"] = "<spring:message code='${fn:toUpperCase(page)}.field.${field.key}'/>";
    </c:forEach>
</script>
<%--    historyJSInit.jsp contains javascript initialisation actions such as initialising strings array for localization
pertaining to revision history components. --%>
<script src="${pageContext.request.contextPath}/js/custom/history.js"></script>
<%--    End of revision history component requirements. --%>
<jsp:include page="../dialogs/revisionHistoryDialog.jsp">
    <jsp:param name="isDraft" value="${param.isDraft}" />
</jsp:include>
<input type="button" id="showRevisions" class="button" value="<spring:message code='general.buttons.revisionHistory'/>" />