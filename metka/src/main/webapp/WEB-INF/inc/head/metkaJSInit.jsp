<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Include MetkaJS namespace javascript. --%>
<script src="${contextPath}/js/metkaJS/metkaJS.js"></script>
<script src="${contextPath}/js/metkaJS/enums.js"></script>
<script src="${contextPath}/js/metkaJS/l10n.js"></script>
<script src="${contextPath}/js/metkaJS/messageManager.js"></script>
<script src="${contextPath}/js/metkaJS/reference.js"></script>
<script src="${contextPath}/js/metkaJS/tableHandler.js"></script>
<script src="${contextPath}/js/metkaJS/jsConfigUtil.js"></script>
<script src="${contextPath}/js/metkaJS/eventManager.js"></script>
<script src="${contextPath}/js/metkaJS/revisionHistory.js"></script>


<script>
    MetkaJS.Globals.page = "${page}";
    MetkaJS.Globals.contextPath = "${contextPath}";

    // Insert localisation for text DRAFT
    MetkaJS.L10N.put("general.DRAFT", "<spring:message code="general.DRAFT"/>");

    // Insert empty selection row
    MetkaJS.L10N.put("general.list.empty", "<spring:message code="general.list.empty"/>");

    // Insert missing implementation notifications
    MetkaJS.L10N.put("general.errors.title.noImplementation", "<spring:message code='general.errors.title.noImplementation' />");
    MetkaJS.L10N.put("general.errors.container.dialog.noImplementation", "<spring:message code='general.errors.container.dialog.noImplementation' />");

<%-- Initialise single object if applicable --%>
<c:choose>
<c:when test="${not empty single}">
    MetkaJS.SingleObject.id = ${single.id};
    MetkaJS.SingleObject.revision = ${single.revision};
</c:when>
<c:otherwise>
    MetkaJS.SingleObject = null;
</c:otherwise>
</c:choose>

<%-- List displayable errors --%>
<c:if test="${not empty displayableErrors}">
<c:forEach items="${displayableErrors}" var="errorObject">
    <c:if test="${not empty errorObject.title}">MetkaJS.L10N.put("${errorObject.title}", "<spring:message code='${errorObject.title}' />");</c:if>
    MetkaJS.L10N.put("${errorObject.msg}", "<spring:message code='${errorObject.msg}' />");
    MetkaJS.MessageManager.push(MetkaJS.MessageManager.Message("${errorObject.title}", "${errorObject.msg}"));
    <c:forEach items="${errorObject.data}" var="dataStr">
    MetkaJS.MessageManager.topMessage().pushData("<spring:message code='${dataStr}' />");
    </c:forEach>
</c:forEach>
</c:if>

<%-- If page is study then add some variable statistics localisations --%>
<c:if test="${page == 'study'}">
    <c:set var="selectionList" value="${configuration['STUDY'].fields['statisticstype'].selectionList}" />
    <c:forEach items="${configuration['STUDY'].selectionLists[selectionList].options}" var="option">
    MetkaJS.L10N.put("STUDY.${selectionList}.option.${option.value}", "<spring:message code='STUDY.${selectionList}.option.${option.value}' />");
    </c:forEach>
</c:if>
    <%-- If JSConfig JSON is provided insert it to globals. Otherwise MetkaJS.JSConfig will remain null --%>
    <c:if test="${not empty jsConfig}">MetkaJS.JSConfig = JSON.parse('${jsConfig}');</c:if>
    <c:if test="${not empty jsGUIConfig}">MetkaJS.JSGUIConfig = JSON.parse('${jsGUIConfig}');</c:if>
</script>