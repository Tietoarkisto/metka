<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
</head>
<body>
    <jsp:include page="../inc/topMenu.jsp" />
    <script data-main="${pageContext.request.contextPath}/js/page.js" src="${pageContext.request.contextPath}/lib/js/require.js"></script>
</body>
</html>