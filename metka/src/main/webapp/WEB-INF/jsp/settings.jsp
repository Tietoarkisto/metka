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
    <div class="content container">
        <div class="pageTitle row"><spring:message code="title.settings" /></div>
        <%-- Data configuration import --%>
        <form:form method="post" action="/settings/uploadDataConfiguration" enctype="multipart/form-data" modelAttribute="uploadConfig">
            Data konfiguraatio
            <input type="file" name="file" />
            <input type="submit" value="<spring:message code="general.buttons.upload.dataConfiguration" />"/>
        </form:form>
        <br />
        <%-- GUI configuration import --%>
        <form:form method="post" action="/settings/uploadGUIConfiguration" enctype="multipart/form-data" modelAttribute="uploadConfig">
            GUI konfiguraatio
            <input type="file" name="file" />
            <input type="submit" value="<spring:message code="general.buttons.upload.guiConfiguration" />"/>
        </form:form>
        <br />
        <%-- Misc Json import --%>
        <form:form method="post" action="/settings/uploadMiscJson" enctype="multipart/form-data" modelAttribute="uploadMisc">
            MISC Json
            <input type="file" name="file" />
            <input type="submit" value="<spring:message code="general.buttons.upload.miscJson" />"/>
        </form:form>
    </div>
</div>
</body>
</html>