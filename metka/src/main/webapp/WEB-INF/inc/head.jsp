<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<title></title>

<jsp:include page="head/cssInit.jsp" />

<jsp:include page="head/jqueryInit.jsp" />

<jsp:include page="head/metkaJSInit.jsp" />

<jsp:include page="head/guiJSInit.jsp" />

<%-- Include general table handlers for container and reference container tables --%>
<script src="${contextPath}/js/handlers/generalHandlers.js"></script>

<jsp:include page="head/customJSInit.jsp" />