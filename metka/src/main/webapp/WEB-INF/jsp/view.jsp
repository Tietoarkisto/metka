<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div class="content">
        <h1 class="pageTitle"><spring:message code="${fn:toUpperCase(page)}"/> - <spring:message code="${fn:toUpperCase(page)}.field.${configuration.displayId}"/>&nbsp;${single.id} - <spring:message code="general.revision"/>&nbsp;${single.revision}</h1>
        <jsp:include page="../inc/prevNext.jsp" />
        <jsp:include page="${page}/view.jsp" />
        <div class="buttonsHolder">
            <jsp:include page="../inc/revHistory.jsp" />
            <input type="button" class="button"
                   value="<spring:message code='general.buttons.edit'/>"
                   onclick="MetkaJS.SingleObject.edit()"/>
            <jsp:include page="../inc/removeButton.jsp" />
        </div>
    </div>
</div>
</body>
</html>