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
<div class="wrapper">
    <div class="content container">
        <%-- TODO: Get localizations from l10n.js --%>
        <%--<div class="page-header">Asetukset</div>--%>
            <%-- TODO: Change to Ajax calls --%>
        <%-- Data configuration import --%>
        <%--<form:form method="post" action="settings/uploadDataConfiguration" enctype="multipart/form-data" modelAttribute="uploadConfig">
            Data konfiguraatio
            <input type="file" name="file" class="file" data-show-preview="false" data-show-remove="false" data-upload-label="Lataa" data-browse-label="Valitse ..." />
        </form:form>
        <br />--%>
        <%-- GUI configuration import --%>
        <%--<form:form method="post" action="settings/uploadGUIConfiguration" enctype="multipart/form-data" modelAttribute="uploadConfig">
            GUI konfiguraatio
            <input id="input-id" type="file" class="file" data-show-preview="false" data-show-remove="false" data-upload-label="Lataa" data-browse-label="Valitse ..." />
        </form:form>
        <br />--%>
        <%-- Misc Json import --%>
        <%--<form:form method="post" action="/settings/uploadMiscJson" enctype="multipart/form-data" modelAttribute="uploadMisc">
            MISC Json
            <input type="file" name="file" class="file" data-show-preview="false" data-show-remove="false" data-upload-label="Lataa" data-browse-label="Valitse ..." />
        </form:form>
        <br />--%>
        <%--Indekserit
        <table class="formTable">
            <thead>
            <tr>
                <th>Path</th>
                <th>Status</th>
            </tr>
            </thead>
            <c:forEach var="indexer" items="${indexers}">
                <tr>
                    <td>${indexer.key}</td>
                    <td>${indexer.value == true ? "running" : "stopped"}</td>
                </tr>
            </c:forEach>
        </table>

        <button type="button" class="btn btn-default" onclick="require(['./modules/assignUrl'], function (assignUrl) {assignUrl('/settings/indexEverything');})">
            Indeksoi kaikki
        </button>--%>

    </div>
</div>
<script>
    MetkaJS.configurationType = 'SETTINGS';
</script>
<script data-main="${pageContext.request.contextPath}/js/page.js" src="${pageContext.request.contextPath}/lib/js/require.js"></script>
</body>
</html>