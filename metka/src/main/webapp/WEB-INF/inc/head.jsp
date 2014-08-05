<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<title></title>

<jsp:include page="head/cssInit.jsp" />

<jsp:include page="head/jqueryInit.jsp" />

<jsp:include page="head/metkaJSInit.jsp" />

<c:if test="${not empty jsGUIConfig}">
    <script>MetkaJS.jsGUIConfig = ${jsGUIConfig}[MetkaJS.Globals.page.toUpperCase()];</script>
</c:if>

<script src="${contextPath}/lib/js/moment-with-langs.min.js"></script>
<script src="${contextPath}/lib/bootstrap/3.1.1/js/bootstrap.min.js"></script>
<script src="${contextPath}/lib/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
<script src="${contextPath}/lib/bootstrap-fileinput/js/fileinput.min.js"></script>

<%-- Include general table handlers for container and reference container tables --%>
<script src="${contextPath}/js/handlers/generalHandlers.js"></script>

<jsp:include page="head/customJSInit.jsp" />