<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
    <script>
        SingleObject = function() {
            return {
                id: ${single.id},
                revision: ${single.revision},
                draft: false
            };
        }();
    </script>
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div class="content">
        <h1 class="pageTitle"><spring:message code="${fn:toUpperCase(page)}"/> ${single.id} - <spring:message code="general.revision"/> ${single.revision}</h1>
        <jsp:include page="../inc/prevNext.jsp">
            <jsp:param name="id" value="${single.id}" />
        </jsp:include>
        <jsp:include page="${page}/view.jsp" />
        <div class="buttonsHolder">
            <jsp:include page="../inc/revHistory.jsp">
                <jsp:param name="isDraft" value="false"></jsp:param>
            </jsp:include>

            <input type="button" class="button"
                   value="<spring:message code='general.buttons.edit'/>"
                   onclick="location.href='${pageContext.request.contextPath}/${page}/edit/${single.id}'"/>
            <jsp:include page="../inc/removeButton.jsp" />
        </div>
    </div>
</div>
</body>
</html>