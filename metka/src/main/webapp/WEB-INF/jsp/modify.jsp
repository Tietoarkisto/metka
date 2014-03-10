<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
    <script>
        SingleObject = function() {
            return {
                id: ${single.id},
                revision: ${single.revision},
                draft: true
            };
        }();
    </script>
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div class="content">
        <h1 class="pageTitle"><spring:message code="${fn:toUpperCase(page)}"/> ${single.id} - <spring:message code="general.revision"/> ${single.revision} - <spring:message code="general.title.DRAFT"/></h1>
        <jsp:include page="${page}/modify.jsp" />
    </div>
</div>
</body>
</html>