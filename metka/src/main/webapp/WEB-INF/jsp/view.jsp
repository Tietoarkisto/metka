<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div id="dynamicContent" class="content container"></div>
</div>
<script>
    $(document).ready(function () {
        $('#dynamicContent').metkaUI();
    });
</script>
</body>
</html>