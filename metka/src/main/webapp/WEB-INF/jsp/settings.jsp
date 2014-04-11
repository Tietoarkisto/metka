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
    <div class="content">
        <h1 class="pageTitle"><spring:message code="title.settings" /></h1>
        <%-- Configuration import --%>
        <form:form method="post" action="/settings/uploadConfiguration" enctype="multipart/form-data" modelAttribute="uploadConfig">
            <input type="file" name="file" />
            <br />
            <input type="submit" value="<spring:message code="general.buttons.upload.configuration" />"/>
        </form:form>
        <%-- Misc Json import --%>
        <form:form method="post" action="/settings/uploadMiscJson" enctype="multipart/form-data" modelAttribute="uploadMisc">
            <input type="file" name="file" />
            <br />
            <input type="submit" value="<spring:message code="general.buttons.upload.miscJson" />"/>
        </form:form>
    </div>
</div>
</body>
</html>