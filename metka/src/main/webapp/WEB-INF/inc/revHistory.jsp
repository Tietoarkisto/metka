<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- Includes everything needed to implement revision history viewing and comparison -->
<!-- Required params:
        type - Type of object being viewed
        isDraft - Knowledge of is the object being viewed a draft
        id - revisionableId of the object being viewed -->
<!--    Initialization for revision history viewing and comparison. All of these things have to be included when this
functionality is required including a component with id: showRevisions. -->
<script>
    var revisionableId = ${param.id};
    var isDraft = ${param.isDraft};
    var contextPath = "${pageContext.request.contextPath}";
    var type = "${page}";
</script>
<!--    historyJSInit.jsp contains javascript initialisation actions such as initialising strings array for localization
pertaining to revision history components. -->
<%@include file="historyJSInit.jsp"%>
<script src="${pageContext.request.contextPath}/js/custom/history.js"></script>
<!--    End of revision history component requirements. -->
<jsp:include page="../dialogs/revisionHistoryDialog.jsp">
    <jsp:param name="isDraft" value="${param.isDraft}" />
</jsp:include>
<input type="button" id="showRevisions" class="button" value="<spring:message code='general.buttons.revisionHistory'/>" />