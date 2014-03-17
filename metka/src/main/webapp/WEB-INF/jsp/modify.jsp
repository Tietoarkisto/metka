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
        MetkaJS.SingleObject.draft = true;
    </script>
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div class="content">
        <h1 class="pageTitle"><spring:message code="${fn:toUpperCase(page)}"/> - <spring:message code="${fn:toUpperCase(page)}.field.${configuration.displayId}"/>&nbsp;${single.id} - <spring:message code="general.revision"/>&nbsp;${single.revision} - <spring:message code="general.title.DRAFT"/></h1>
        <jsp:include page="${page}/modify.jsp" />
        <div class="buttonsHolder">
            <input type="button" id="revisionModifyFormSave" class="button" value="<spring:message code="general.buttons.save" />">
            <input type="button" id="revisionModifyFormApprove" class="button" value="<spring:message code='general.buttons.approve'/>" />
            <jsp:include page="../inc/revHistory.jsp"/>
            <jsp:include page="../inc/removeButton.jsp" />
            <!-- TODO: Fix this reset button
            <input type="reset" class="button" value="Tyhjennä">-->
        </div>
    </div>
</div>
</body>
</html>